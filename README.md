how to use sqlite_lib

1.select data
```
		SqliteCreator mSqliteCreator = new SqliteCreator(mContext);
		mSqliteCreator.init("user_data");
		ArrayList<ContentValues> result = mSqliteCreator.excuteSelect();
		for (int i = 0; i < result.size(); i++) {
			ContentValues row = result.get(i);
			
			// get string
			Log.e("Data", " name: " + row.get("name"));

			// get get integer
			int s_day = row.getAsInteger("s_day");
		}
```		
2. select data with filter field
```
		// check data exist or not
		SqliteCreator mSqliteCreator = new SqliteCreator(context);
		mSqliteCreator.init("user_data");
		mSqliteCreator.addReturnFields("name");
		mSqliteCreator.addQuery("name='" + dataName + "'");
		ArrayList<ContentValues> result = mSqliteCreator.excuteSelect();
		
		 for (int i = 0; i < result.size(); i++) {
		 	ContentValues row = result.get(i); 
		 	Log.e("Data", "type: " + row.get("data_type") + " name: " + row.get("name")); 
		 }
```
3.save data
```
	public void saveData(String dataName) {

		// check data exist or not
		SqliteCreator mSqliteCreator = new SqliteCreator(context);
		mSqliteCreator.init("user_data");
		mSqliteCreator.addReturnFields("name");
		mSqliteCreator.addQuery("name='" + dataName + "'");
		ArrayList<ContentValues> result = mSqliteCreator.excuteSelect();
		if (result.size() > 0) {
			String errorMsg = "* " + dataName + " existing !";
			showInputDialog(dataName, errorMsg);
			return;
		}

		// save data
		mSqliteCreator.init("user_data");
		mSqliteCreator.addData("name", dataName);
		mSqliteCreator.addData("s_day", s_day);
		mSqliteCreator.addData("s_month", s_month);
		mSqliteCreator.addData("s_year", s_year);
		mSqliteCreator.addData("hour", hour);
		mSqliteCreator.addData("minute", minute);

		boolean result2 = mSqliteCreator.executeInsert();

		Log.e("Data", "Insert: " + result2);
		Log.e("Data", "Error: " + mSqliteCreator.getError());
	}
```	
4. delete data
```
	// check data exist or not
	SqliteCreator mSqliteCreator = new SqliteCreator(context);
	mSqliteCreator.init("user_data");
	mSqliteCreator.addQuery("name = '"+the.tenluu+"'");
	boolean result = mSqliteCreator.executeDelete();
	if (result){
		Log.e("Data","deleted success");
	}
```