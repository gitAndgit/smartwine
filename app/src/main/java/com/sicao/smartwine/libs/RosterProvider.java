package com.sicao.smartwine.libs;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

import com.smartline.life.core.BaseContentProvider;

import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.roster.rosterstore.RosterStore;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.util.XmppStringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class RosterProvider extends BaseContentProvider {

	private static final boolean DEBUG = false;
	private static final String TAG = "RosterContentProvider";

	private ConnectionCreationListener mConnectionCreationListener = new ConnectionCreationListener() {
		@Override
		public void connectionCreated(XMPPConnection connection) {
			if (DEBUG){
				Log.e(TAG,"connectionCreated.hashCode="+connection.hashCode());
			}
			Roster roster = Roster.getInstanceFor(connection);
			roster.setRosterStore(new MyRosterStore(connection,roster));
			roster.setRosterLoadedAtLogin(true);
		}
	};

	@Override
	public boolean onCreate() {
		super.onCreate();
		XMPPConnectionRegistry.addConnectionCreationListener(mConnectionCreationListener);
		return true;
	}

	@Override
	protected SQLiteOpenHelper onCreateSQLiteOpenHelper() {
		return new RosterSQLiteOpenHelper(getContext());
	}

	private Uri getRosterUri(){
		return Uri.withAppendedPath(getAuthorityUri(), Contracts.Rosters.TABLE_NAME);
	}

	private Uri getRosterGroupUri(){
		return Uri.withAppendedPath(getAuthorityUri(), Contracts.RosterGroups.TABLE_NAME);
	}


	private Uri getVCardUri(){
		return Uri.withAppendedPath(getAuthorityUri(), Contracts.VCards.TABLE_NAME);
	}

	private class MyRosterStore implements RosterStore,RosterListener {

		private XMPPConnection mConnection;
		private Roster mRoster;

		public MyRosterStore(XMPPConnection connection,Roster roster){
			mConnection = connection;
			mRoster = roster;
			mRoster.addRosterListener(this);
		}

		@Override
		public Collection<RosterPacket.Item> getEntries() {
			List<RosterPacket.Item> entries = new ArrayList<RosterPacket.Item>();

			Cursor c = query(getRosterUri(), null, null, null, null);
			while (c.moveToNext()){
				String jid = c.getString(c.getColumnIndex(Contracts.RosterColumns.JID));
				String name = c.getString(c.getColumnIndex(Contracts.RosterColumns.NAME));
				String type = c.getString(c.getColumnIndex(Contracts.RosterColumns.TYPE));
				String status = c.getString(c.getColumnIndex(Contracts.RosterColumns.STATUS));

				RosterPacket.Item item = new RosterPacket.Item(jid, name);
				item.setItemStatus(RosterPacket.ItemStatus.fromString(status));
				item.setItemType(RosterPacket.ItemType.valueOf(type));

				List<String> groups = queryRosterGroups(jid);
				for (String groupName:groups) {
					item.addGroupName(groupName);
				}
				entries.add(item);
			}
			c.close();
			return entries;
		}

		@Override
		public RosterPacket.Item getEntry(String bareJid) {
			RosterPacket.Item item = null;
			String selection = Contracts.RosterColumns.JID+"=?";
			String[] selectionArgs = {bareJid};
			Cursor c = query(getRosterUri(), null, selection, selectionArgs, null);
			if (c.moveToFirst()){
				String jid = c.getString(c.getColumnIndex(Contracts.RosterColumns.JID));
				String name = c.getString(c.getColumnIndex(Contracts.RosterColumns.NAME));
				String type = c.getString(c.getColumnIndex(Contracts.RosterColumns.TYPE));
				String status = c.getString(c.getColumnIndex(Contracts.RosterColumns.STATUS));

				item = new RosterPacket.Item(jid, name);
				item.setItemStatus(RosterPacket.ItemStatus.fromString(status));
				item.setItemType(RosterPacket.ItemType.valueOf(type));

				List<String> groups = queryRosterGroups(jid);
				for (String groupName:groups) {
					item.addGroupName(groupName);
				}
			}
			c.close();
			return item;
		}

		@Override
		public String getRosterVersion() {
			return ""+System.currentTimeMillis();
		}

		@Override
		public boolean addEntry(RosterPacket.Item item, String version) {
			if (DEBUG){
				Log.e("MyRosterStore", "addEntry=" + item.getUser());
			}
			if (item.getItemStatus() == RosterPacket.ItemStatus.unsubscribe){
				return false;
			}
			ContentValues values = new ContentValues();
			values.put(Contracts.RosterColumns.NAME,item.getName());
			values.put(Contracts.RosterColumns.TYPE,item.getItemType() != null ?item.getItemType().toString():null);
			values.put(Contracts.RosterColumns.STATUS,item.getItemStatus() != null ? item.getItemStatus().toString():null);
			values.put(Contracts.RosterColumns.ONLINE, mRoster.getPresence(item.getUser()).isAvailable());

			Cursor c = query(getRosterUri(),null, Contracts.Rosters.JID+"=?",new String[]{item.getUser()},null);
			if (c.moveToFirst()){
				long id = c.getLong(c.getColumnIndex(Contracts.Rosters._ID));
				update(ContentUris.withAppendedId(getRosterUri(), id), values, null, null);
			}else {
				values.put(Contracts.RosterColumns.JID, item.getUser());
				insert(getRosterUri(), values);
			}
			Set<String> groups = item.getGroupNames();
			for (String groupName : groups){
				if (!hasRosterGroup(item.getUser(),groupName)){
					insertRosterGroup(item.getUser(),groupName);
				}
			}
			new VCardLoad(item.getUser()).start();
			return true;
		}

		@Override
		public boolean resetEntries(Collection<RosterPacket.Item> items, String version) {
			if (DEBUG){
				Log.e("MyRosterStore","resetEntries");
			}
			delete(getRosterUri(), null, null);
			delete(getRosterGroupUri(), null, null);
			delete(getVCardUri(), null, null);
			getContext().getContentResolver().delete(DeviceMetaData.CONTENT_URI, null, null);

			List<ContentValues> rosters = new ArrayList<ContentValues>();
			List<ContentValues> rosterGroups = new ArrayList<ContentValues>();
			for (RosterPacket.Item item : items){

				ContentValues value = new ContentValues();
				value.put(Contracts.RosterColumns.JID,item.getUser());
				value.put(Contracts.RosterColumns.NAME,item.getName());
				value.put(Contracts.RosterColumns.TYPE,item.getItemType() != null ?item.getItemType().toString():null);
				value.put(Contracts.RosterColumns.STATUS,item.getItemStatus() != null ? item.getItemStatus().toString():null);
				value.put(Contracts.RosterColumns.ONLINE, mRoster.getPresence(item.getUser()).isAvailable());
				rosters.add(value);

				Set<String> groups = item.getGroupNames();
				for (String groupName : groups){
					ContentValues group = new ContentValues();
					group.put(Contracts.RosterGroupColumns.JID,item.getUser());
					group.put(Contracts.RosterGroupColumns.NAME,groupName);
					rosterGroups.add(group);
				}
			}

			ContentValues[] rosterArrays = new ContentValues[rosters.size()];
			rosters.toArray(rosterArrays);
			bulkInsert(getRosterUri(), rosterArrays);

			ContentValues[] rosterGroupsArrays = new ContentValues[rosterGroups.size()];
			rosterGroups.toArray(rosterGroupsArrays);
			bulkInsert(getRosterGroupUri(), rosterGroupsArrays);

			new VCardLoad().start();
			return true;
		}

		@Override
		public boolean removeEntry(String bareJid, String version) {
			delete(getRosterUri(),Contracts.RosterGroupColumns.JID + "=?", new String[]{bareJid});
			delete(getRosterGroupUri(), Contracts.RosterGroupColumns.JID + "=?", new String[]{bareJid});
			delete(getVCardUri(), Contracts.RosterGroupColumns.JID + "=?", new String[]{bareJid});
			return true;
		}

		class VCardLoad extends Thread{

			private  String jid;

			public VCardLoad(){

			}

			public VCardLoad(String jid){
				this.jid = jid;
			}

			@SuppressLint("NewApi") @Override
			public void run() {
				if (jid != null){
					loadVcard(jid);
				}else {
					loadVcard(null);
					Cursor c = query(getRosterUri(),null,null,null,null,null);
					while (c.moveToNext()){
						String jid = c.getString(c.getColumnIndex(Contracts.RosterColumns.JID));
						loadVcard(jid);
					}
					c.close();
				}
			}

			private void loadVcard(String jid){
				ContentResolver resolver = getContext().getContentResolver();
				VCardManager vCardManager = VCardManager.getInstanceFor(mConnection);
				try {
					VCard vCard = vCardManager.loadVCard(jid);
					String model = vCard.getField("MODEL");
					String type = vCard.getField("TYPE");
					Roster roster = Roster.getInstanceFor(mConnection);
					if (model != null && type != null){
						if (!DeviceUtil.isSupportDevice(model)){
							return;
						}
						RosterEntry entry = roster.getEntry(jid);
						if (entry == null){
							return;
						}
						ContentValues value = new ContentValues();
						value.put(DeviceMetaData.NAME, entry.getName());
						value.put(DeviceMetaData.NICK,vCard.getField("NAME"));
						value.put(DeviceMetaData.TYPE,type.toLowerCase());
						value.put(DeviceMetaData.MODEL,model.toLowerCase());
						value.put(DeviceMetaData.ONLINE,roster.getPresence(jid).isAvailable());
						value.put(DeviceMetaData.VERSION,vCard.getField("VERSION"));
						value.put(DeviceMetaData.MAN,vCard.getField("MAN").toLowerCase());
						value.put(DeviceMetaData.CATEGORY,vCard.getField("CATEGORY").toLowerCase());
						value.put(DeviceMetaData.MAC,vCard.getField("MAC"));
						value.put(DeviceMetaData.OS,vCard.getField("OS"));
						value.put(DeviceMetaData.SSID, vCard.getField("WIFI.SSID"));
						value.put(DeviceMetaData.BSSID, vCard.getField("WIFI.BSSID"));
						value.put(DeviceMetaData.WIFIPASSWD, vCard.getField("WIFI.PASSWORD"));

						if (entry.getGroups().size() > 0){
							RosterGroup group = entry.getGroups().get(0);
							value.put(DeviceMetaData.GROUP, group.getName());
						}else {
							value.put(DeviceMetaData.GROUP, "我的设备");
						}

						Cursor c = resolver.query(DeviceMetaData.CONTENT_URI, null, DeviceMetaData.JID + "=?", new String[]{jid}, null);
						if (c.moveToFirst()){
							long id = c.getLong(c.getColumnIndex(DeviceMetaData._ID));
							resolver.update(ContentUris.withAppendedId(DeviceMetaData.CONTENT_URI, id), value, null, null);
						}else {
							value.put(DeviceMetaData.JID,jid);
							resolver.insert(DeviceMetaData.CONTENT_URI, value);
						}
						c.close();
						Log.e(TAG, value.toString());
						if (roster.getPresence(jid).isAvailable()){
							value = new ContentValues();
							value.put(DeviceMetaData.NAME,roster.getEntry(jid).getName());
							value.put(DeviceMetaData.STATUS, DeviceMetaData.Status.WAN.toString());
							resolver.update(DeviceMetaData.FIND_CONTENT_URI, value, DeviceMetaData.JID + "=?", new String[]{jid});
						}
					}else {
						if (jid == null){
							jid = mConnection.getUser();
						}
						ContentValues value = new ContentValues();
						value.put(Contracts.VCardColumns.NAME,roster.getEntry(jid) == null ? null: roster.getEntry(jid).getName());
						value.put(Contracts.VCardColumns.NICKNAME,vCard.getNickName());
						value.put(Contracts.VCardColumns.FIRSTNAME,vCard.getFirstName());
						value.put(Contracts.VCardColumns.LASTNAME,vCard.getLastName());
						value.put(Contracts.VCardColumns.MIDDLENAME,vCard.getMiddleName());
						value.put(Contracts.VCardColumns.AVATAR,vCard.getAvatar());
						value.put(Contracts.VCardColumns.EMAIL_HOME,vCard.getEmailHome());
						value.put(Contracts.VCardColumns.EMAIL_WORK,vCard.getEmailWork());
						value.put(Contracts.VCardColumns.MALE, vCard.getField("MALE"));
						value.put(Contracts.VCardColumns.BIRTHDAY, vCard.getField("BIRTHDAY"));
						value.put(Contracts.VCardColumns.TIMESTAMP, SystemClock.uptimeMillis());
						Cursor c = query(getVCardUri(), null, Contracts.VCardColumns.JID + "=?", new String[]{jid}, null);
						if (c.moveToFirst()){
							long id = c.getLong(c.getColumnIndex(Contracts.VCardColumns._ID));
							update(ContentUris.withAppendedId(getVCardUri(), id), value, null, null);
						}else {
							value.put(Contracts.VCardColumns.JID,jid);
							insert(getVCardUri(), value);
						}
						c.close();
						Log.e(TAG, value.toString());
					}
				} catch (SmackException.NoResponseException e) {
					e.printStackTrace();
				} catch (XMPPException.XMPPErrorException e) {
					e.printStackTrace();
				} catch (SmackException.NotConnectedException e) {
					e.printStackTrace();
				}
			}
		}


		public List<String> queryRosterGroups(String bareJid){
			List<String> groups = new ArrayList<String>();
			Cursor c = query(getRosterGroupUri(), null, Contracts.RosterGroupColumns.JID+"=?",new String[]{bareJid}, null);
			while (c.moveToNext()){
				groups.add(c.getString(c.getColumnIndex(Contracts.RosterGroupColumns.NAME)));
			}
			c.close();
			return groups;
		}

		public boolean hasRosterGroup(String bareJid,String groupName){
			boolean has;
			Cursor c = query(getRosterGroupUri(),null, Contracts.RosterGroupColumns.JID+"=? and "+ Contracts.RosterGroupColumns.NAME+"=?",new String[]{bareJid,groupName},null);
			has = c.moveToFirst();
			c.close();
			return has;
		}

		public void insertRosterGroup(String bareJid,String groupName){
			ContentValues values = new ContentValues();
			values.put(Contracts.RosterGroupColumns.JID,bareJid);
			values.put(Contracts.RosterGroupColumns.NAME, groupName);
			insert(getRosterGroupUri(), values);
		}


		@Override
		public void entriesAdded(Collection<String> addresses) {

		}

		@Override
		public void entriesUpdated(Collection<String> addresses) {

		}

		@Override
		public void entriesDeleted(Collection<String> addresses) {

		}

		@Override
		public void presenceChanged(Presence presence) {
			String jid = XmppStringUtils.parseBareJid(presence.getFrom());
			ContentValues value = new ContentValues();
			value.put(Contracts.RosterColumns.ONLINE, presence.isAvailable());
			update(getRosterUri(), value, Contracts.RosterColumns.JID + "=?", new String[]{jid});
		}
	}

	private static class RosterSQLiteOpenHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "roster.db";
		private static final int DATABASE_VERSION = 1;

		public RosterSQLiteOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String rostersSql = "CREATE TABLE IF NOT EXISTS "
					+ Contracts.Rosters.TABLE_NAME
					+"("
					+ Contracts.RosterColumns._ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ Contracts.RosterColumns.JID +" TEXT UNIQUE,"
					+ Contracts.RosterColumns.NAME + " TEXT,"
					+ Contracts.RosterColumns.TYPE + " TEXT,"
					+ Contracts.RosterColumns.STATUS + " TEXT,"
					+ Contracts.RosterColumns.ONLINE + "  INTEGER"
					+")";

			db.execSQL(rostersSql);

			String rosterGroupsSql = "CREATE TABLE IF NOT EXISTS "
					+ Contracts.RosterGroups.TABLE_NAME
					+"("
					+ Contracts.RosterGroupColumns._ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ Contracts.RosterGroupColumns.JID +" TEXT NOT NULL,"
					+ Contracts.RosterGroupColumns.NAME + " TEXT NOT NULL"
					+")";

			db.execSQL(rosterGroupsSql);

			String vcardsSql = "CREATE TABLE IF NOT EXISTS "
					+ Contracts.VCards.TABLE_NAME
					+"("
					+ Contracts.VCardColumns._ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ Contracts.VCardColumns.JID +" TEXT NOT NULL,"
					+ Contracts.VCardColumns.NAME + " TEXT,"
					+ Contracts.VCardColumns.NICKNAME + " TEXT,"
					+ Contracts.VCardColumns.FIRSTNAME + " TEXT,"
					+ Contracts.VCardColumns.LASTNAME + " TEXT,"
					+ Contracts.VCardColumns.AVATAR + " BLOB,"
					+ Contracts.VCardColumns.MIDDLENAME + " TEXT,"
					+ Contracts.VCardColumns.EMAIL_HOME + " TEXT,"
					+ Contracts.VCardColumns.EMAIL_WORK + " TEXT,"
					+ Contracts.VCardColumns.MALE + " TEXT,"
					+ Contracts.VCardColumns.BIRTHDAY + " TEXT,"
					+ Contracts.VCardColumns.TIMESTAMP + " INTEGER"
					+")";
			db.execSQL(vcardsSql);
		}




		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table if exists "+ Contracts.Rosters.TABLE_NAME + ";");
			db.execSQL("drop table if exists "+ Contracts.RosterGroups.TABLE_NAME + ";");
			db.execSQL("drop table if exists "+ Contracts.VCards.TABLE_NAME + ";");
			onCreate(db);
		}
	}

}
