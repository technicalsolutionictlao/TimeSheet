package com.ictlao.android.app.timesheet.Manager;

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.YEAR;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ictlao.android.app.timesheet.Items.AdminItems;
import com.ictlao.android.app.timesheet.Items.DataResult;
import com.ictlao.android.app.timesheet.Items.MessageItems;
import com.ictlao.android.app.timesheet.Items.TimeSheetItems;
import com.ictlao.android.app.timesheet.Items.UserItems;
import com.ictlao.android.app.timesheet.Items.UserStatusItems;
import com.ictlao.android.app.timesheet.Items.VacationItems;
import com.ictlao.android.app.timesheet.Items.WorkTimeItems;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
// all data and useful function
public class DataManager {

    public static final String AUTO_SET = "AutoSet";
    // Part
    public static class Part {
        // declare part name
        public static class PartName {
            public static final String Morning = "ເຊົ້າ";
            public static final String AfterNoon = "ບ່າຍ";
            public static final String Evening = "ຄໍ່າ";
        }

        // default list of all company
        public static ArrayList<WorkTimeItems> getDefault(){
            ArrayList<WorkTimeItems> list = new ArrayList<>();
            WorkTimeItems items = new WorkTimeItems(PartName.Morning,"8:30","12:00");
            WorkTimeItems items1 = new WorkTimeItems(PartName.AfterNoon,"13:00","17:00");
            list.add(items);
            list.add(items1);
            return list;
        }

        // default morning part
        public static WorkTimeItems getMorningPart(){
            return new WorkTimeItems(PartName.Morning,"8:30","12:00");
        }

        // default afternoon part
        public static WorkTimeItems getAfterNoonPart(){
            return new WorkTimeItems(PartName.AfterNoon,"13:00","17:00");
        }

        // check for the user has forgot check out
        /*public static boolean IsForgotCheckOut(UserStatusItems userStatusItems,TimeSheetItems lastItems,boolean isToday){
            if(isToday){
                if (lastItems.getCheckStatus().equals(CHECK_IN)) {
                    int u_hour = getHour(userStatusItems.getTime());
                    int u_minute = getMinute(userStatusItems.getTime());
                    if(u_hour >= 12){
                        if(u_minute >= 30){
                            TimeSheetItems items = new TimeSheetItems(getCurrentTime(),
                                    getFirebaseUser().getUid(),getCurrentDate(),
                                    CHECK_OUT,getCurrentTimeMarker(),
                                    getMorningPart().getTimeOut(),lastItems.getLatitude(),
                                    lastItems.getLongitude(),AUTO_SET);
                            setTimeSheet(items);
                            return true;
                        }
                    }
                    return false;
                }
            }else {
                if (lastItems.getCheckStatus().equals(CHECK_IN)) {
                    int u_hour = getHour(userStatusItems.getTime());
                    if(u_hour <= 12){
                        TimeSheetItems items = new TimeSheetItems(getCurrentTime(),
                                getFirebaseUser().getUid(),lastItems.getDate(),
                                CHECK_OUT,lastItems.getTimeInMarker(),
                                getMorningPart().getTimeOut(),lastItems.getLatitude(),
                                lastItems.getLongitude(),AUTO_SET);
                        setTimeSheet(items);
                    }else{
                        TimeSheetItems items = new TimeSheetItems(getCurrentTime(),
                                getFirebaseUser().getUid(),lastItems.getDate(),
                                CHECK_OUT,lastItems.getTimeInMarker(),
                                getAfterNoonPart().getTimeOut(),lastItems.getLatitude(),
                                lastItems.getLongitude(),AUTO_SET);
                        setTimeSheet(items);
                    }
                    return true;
                }
            }
            return false;
        }*/
    }

    public static final String CHECK_IN = "IN";
    public static final String CHECK_OUT = "OUT";
    public static final String IN = "ເຂົ້າ";
    public static final String OUT = "ອອກ";

    public static final String APPLICATION_NAME = "TimeSheet";
    public static final String MESSAGE = "MESSAGE";
    public static final String STATUS = "STATUS";
    public static final String VACATION = "VACATION";
    public static final String CHECK_IN_OUT = "CHECK_IN_OUT";
    public static final String ACCOUNT = "ACCOUNT";
    //public static final String DEFAULT_WORKING_TIME = "DEFAULT_WORKING_TIME";

    //public static final String ADMIN = "ADMIN";
    //public static final String ADMIN_INFO = "INFO";

    public static final String DASH = "-";
    public static final String COLON = ":";
    public static final String COMPANY = "COMPANY";

    private static FirebaseUser firebaseUser;
    private static DatabaseReference databaseReference;
    private static UserItems userItems;
    private static ArrayList<UserItems> userItemsArrayList;
    private static AdminItems adminItems;

    //private static ArrayList<WorkTimeItems> workTimeItemsArrayList;

    // get this instance for initlialize database
    public static void getInstance(){
        databaseReference = FirebaseDatabase.getInstance().getReference(APPLICATION_NAME);
    }

    // loading vacation data from database
    public static void onNotifyNumberListener(UserItems items, DataResult result){
        DatabaseReference database = databaseReference.child(VACATION).child(getCurrentYM());
        database.child(items.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                result.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.onError(error);
            }
        });
    }

    // use for many companies
    /*public static void onAdminInitialize(String secretCode){
        if(!secretCode.equals("")){
            onDefaultWorkingTimeListener(secretCode, new DataResult() {
                @Override
                public void onSuccess(DataSnapshot snapshot) {
                    if(snapshot.hasChildren()){
                        workTimeItemsArrayList = new ArrayList<>();
                        for(DataSnapshot data : snapshot.getChildren()){
                            WorkTimeItems items = data.getValue(WorkTimeItems.class);
                            if(items != null){
                                workTimeItemsArrayList.add(items);
                            }
                        }
                    }else{
                        workTimeItemsArrayList = Part.getDefault();
                    }
                }

                @Override
                public void onError(DatabaseError error) {

                }
            });
        }
    }

    public static ArrayList<WorkTimeItems> getWorkTimeItemsArrayList() {
        return workTimeItemsArrayList;
    }

    public static void setDefaultWorkingTime(String secretCode, WorkTimeItems items){
        DatabaseReference database = databaseReference.child(DEFAULT_WORKING_TIME);
        database.child(secretCode).push().setValue(items);
    }

    public static void onDefaultWorkingTimeListener(String secretCode,DataResult result){
        DatabaseReference database = databaseReference.child(DEFAULT_WORKING_TIME);
        database.child(secretCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                result.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.onError(error);
            }
        });
    }*/

    // get admin items
    public static AdminItems getAdminItems() {
        return adminItems;
    }

    // set admin items
    public static void setAdminItems(AdminItems adminItems) {
        DataManager.adminItems = adminItems;
    }

    // set company with admin items
    public static void setCompany(AdminItems items){
        DatabaseReference database = databaseReference.child(COMPANY);
        database.child(items.getCompanyId()).setValue(items);
    }

    // load company data from database
    public static void onCompanyListener(DataResult result){
        DatabaseReference database = databaseReference.child(COMPANY);
        database.child(getFirebaseUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                result.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.onError(error);
            }
        });
    }

    // get key of the all data
    public static void onYMListener(DataResult result){
        DatabaseReference database = databaseReference.child(CHECK_IN_OUT);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                result.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.onError(error);
            }
        });
    }

    /*public static ArrayList<UserItems> getUserItemsArrayList() {
        return userItemsArrayList;
    }*/

    // load all users items
    public static void onLoadUsers(){
        userItemsArrayList = new ArrayList<>();
        DatabaseReference database = databaseReference.child(ACCOUNT);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    UserItems items = data.getValue(UserItems.class);
                    if(items != null) {
                        userItemsArrayList.add(items);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // set vacations items to database
    public static void setVacation(VacationItems vacation){
        DatabaseReference database = databaseReference.child(VACATION).child(getCurrentYM()).child(getFirebaseUser().getUid());
        database.push().setValue(vacation);
    }

    // load vacation items from database
    public static void onVacationListener(int year, int month,DataResult result){
        DatabaseReference database = databaseReference.child(VACATION).child(getWithDash(year,month));
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                result.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.onError(error);
            }
        });
    }

    // load vacation items from database
    public static void onVacationListener(String userId,String date,DataResult result){
        DatabaseReference database = databaseReference.child(VACATION).child(getCurrentYM(date)).child(userId);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                result.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.onError(error);
            }
        });
    }

    // set user items
    public static void setUserItems(UserItems items){
        userItems = items;
    }

    // get user items
    public static UserItems getUserItems(){
        return userItems;
    }

    // set user to database
    public static void setUser(FirebaseUser user){
        UserItems items = new UserItems(user.getDisplayName(),user.getUid(),user.getEmail(),
                Objects.requireNonNull(user.getPhotoUrl()).toString(),0,0);
        DatabaseReference database = databaseReference.child(ACCOUNT);
        database.child(items.getUserId()).setValue(items);
    }

    // update user to database
    public static void updateUser(UserItems items){
        DatabaseReference database = databaseReference.child(ACCOUNT).child(items.getUserId());
        database.setValue(items);
    }

    // load all user from database
    public static void onUserListener(DataResult result){
        DatabaseReference database = databaseReference.child(ACCOUNT);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                result.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.onError(error);
            }
        });
    }

    // load current user from database
    public static void onCurrentUserListener(DataResult result){
        DatabaseReference database = databaseReference.child(ACCOUNT).child(getFirebaseUser().getUid());
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                result.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.onError(error);
            }
        });
    }

    // load user from database
    public static void onUserChangeListener(DataResult result){
        DatabaseReference database = databaseReference.child(ACCOUNT);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                result.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.onError(error);
            }
        });
    }

    // deprecate api
    /*public static void onAdminListener(DataResult result){
        DatabaseReference database = databaseReference.child(ACCOUNT).child(ADMIN).child(ADMIN_INFO);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                result.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.onError(error);
            }
        });
    }*/

    // set user status to database
    public static void setUserStatus(UserStatusItems userStatus){
        DatabaseReference database = databaseReference.child(STATUS);
        database.child(userStatus.getUserId()).setValue(userStatus);
    }

    // load user status from database
    public static void onStatusListener(DataResult result){
        DatabaseReference database = databaseReference.child(STATUS).child(getFirebaseUser().getUid());
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                result.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.onError(error);
            }
        });
    }

    // set timesheet items to database
    /*public static void setTimeSheet(UserItems userItems,String checkStatus,double latitude,double longitude){
        TimeSheetItems sheetItems = new TimeSheetItems(getCurrentDate()+getCurrentTime(),userItems.getUserId(),
                getCurrentDate(),)
        DatabaseReference database = databaseReference.child(CHECK_IN_OUT).child(getCurrentYM()).child(sheetItems.getUserId());
        database.push().setValue(sheetItems);
    }*/

    // set time sheet items to database
    public static void setTimeSheet(TimeSheetItems items){
        DatabaseReference database = databaseReference.child(CHECK_IN_OUT).child(getCurrentYM()).child(items.getUserId());
        database.push().setValue(items);
    }

    // set timesheet items to database
    public static void setTimeSheet(TimeSheetItems items,int year, int month,String push){
        DatabaseReference database = databaseReference.child(CHECK_IN_OUT)
                .child(getWithDash(year,month))
                .child(items.getUserId())
                .child(push);
        database.setValue(items);
    }

    public static void setTimeSheet(TimeSheetItems items,String date,String push){
        DatabaseReference database = databaseReference.child(CHECK_IN_OUT)
                .child(getCurrentYM(date))
                .child(items.getUserId())
                .child(push);
        database.setValue(items);
    }

    //load timesheet from database
    public static void onTimeSheetListener(DataResult result){
        DatabaseReference database = databaseReference.child(CHECK_IN_OUT).child(getCurrentYM()).child(getFirebaseUser().getUid());
        database.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        result.onSuccess(snapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        result.onError(error);
                    }
                });
    }

    // load timesheet from database
    public static void onTimeSheetListener(String userId,String date,DataResult result){
        DatabaseReference database = databaseReference.child(CHECK_IN_OUT).child(getCurrentYM(date)).child(userId);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                result.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.onError(error);
            }
        });
    }

    // load time sheet from database
    public static void onTimeSheetListener(String userId,int year,int month,DataResult result){
        DatabaseReference database = databaseReference.child(CHECK_IN_OUT).child(getWithDash(year,month)).child(userId);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                result.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.onError(error);
            }
        });
    }

    // load timesheet from database
    public static void onTimeSheetListener(int year,int month,DataResult result){
        DatabaseReference database = databaseReference.child(CHECK_IN_OUT).child(getWithDash(year,month));
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                result.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.onError(error);
            }
        });
    }

    // get timesheet items with the given date
    public static ArrayList<TimeSheetItems> getTimeSheetItemsWith(ArrayList<TimeSheetItems> timeSheetItems,String date){
        ArrayList<TimeSheetItems> list = new ArrayList<>();
        for(int i = 0; i < timeSheetItems.size(); i++){
            TimeSheetItems items = timeSheetItems.get(i);
            if(items.getDate().equals(date)){
                list.add(items);
            }
        }
        return list;
    }

    // get push key with the given date
    public static ArrayList<String> getPushesWith(ArrayList<TimeSheetItems> timeSheetItems,
                                                                  String date,ArrayList<String> pushes){
        ArrayList<String> list = new ArrayList<>();
        for(int i = 0; i < timeSheetItems.size(); i++){
            TimeSheetItems items = timeSheetItems.get(i);
            String push = pushes.get(i);
            if(items.getDate().equals(date)){
                list.add(push);
            }
        }
        return list;
    }

    // set notification message to database
    public static void setNotificationMessage(MessageItems message){
        DatabaseReference database = databaseReference.child(MESSAGE);
        database.push().setValue(message);
    }

    // load notification message from database
    public static void onNotificationMessageListener(DataResult result){
        DatabaseReference database = databaseReference.child(MESSAGE);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                result.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result.onError(error);
            }
        });
    }

    // get firebase user from the login email
    public static FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    // set firebase user
    public static void setFirebaseUser(FirebaseUser firebaseUser) {
        DataManager.firebaseUser = firebaseUser;
    }

    // get current date with format yyyy-MM-dd
    public static String getCurrentDate(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(Calendar.getInstance().getTime());
    }

    // get current time with format HH:mm
    public static String getCurrentTime(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(Calendar.getInstance().getTime());
    }

    // get time marker with format a
    public static String getCurrentTimeMarker(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("a");
        return format.format(Calendar.getInstance().getTime());
    }

    // get current year and month with format yyyy-MM
    public static String getCurrentYM(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(Calendar.getInstance().getTime());
    }

    // get year and month from date given
    public static String getCurrentYM(String date){
        if(date.equals(""))return date;
        String[] strings = date.split(DASH);
        String s = date;
        if(strings.length > 1){
            s = strings[0]+DASH+strings[1];
        }
        return s;
    }

    // check if user pass the finger print button again
    public static boolean isPressAgain(String time){
        if(time.equals(""))return false;
        boolean b = false;
        int oH = getHour(time);
        int oM = getMinute(time);
        int cH = getHour(getCurrentTime());
        int cM = getMinute(getCurrentTime());
        if(oH == cH){
            if((oM + 10) >= cM){
                b = true;
            }
        }else{
            if((oH + 1) == cM){
                int n = 60 - oM;
                if((n + 10) >= cM){
                    b = true;
                }
            }
        }
        /*String[] texts = time.split(COLON);
        if(texts.length == 2){
            String cur = getCurrentTime();
            String[] curText = cur.split(COLON);
            if(texts[0].equals(curText[0])){
                int t = convertToInt(texts[1]);
                int c = convertToInt(curText[1]);
                if((t + 10) >= c){
                    b = true;
                }
            }
        }*/
        return b;
    }

    private static long getMinute(long seconds){
        return TimeUnit.SECONDS.toMinutes(seconds);
    }

    // convert text to number
    public static int convertToInt(String text){
        if(text.equals(""))return -1;
        int i = -1;
        try{
            i = Integer.parseInt(text);
        }catch (Exception e){
            e.printStackTrace();
        }
        return i;
    }

    // convert text to number
    public static int getInteger(String text){
        if(text.equals(""))return 0;
        int i = 0;
        try{
            i = Integer.parseInt(text);
        }catch (Exception e){
            e.printStackTrace();
        }
        return i;
    }

    // convert number to text
    public static String getString(int i){
        String s = "";
        try{
            s = String.valueOf(i);
        }catch (Exception e){
            e.printStackTrace();
        }
        return s;
    }

    // convert long number to text
    public static String getString(long l){
        String s = "";
        try{
            s = String.valueOf(l);
        }catch (Exception e){
            e.printStackTrace();
        }
        return s;
    }

    // check if text given is the current day
    public static boolean IsToday(String s){
        if(s.equals(""))return false;
        boolean b = false;
        String[] strings = s.split(DASH);
        if(strings.length == 3){
            int year = convertToInt(strings[0]);
            int month = convertToInt(strings[1]);
            int day = convertToInt(strings[2]);
            if(year == getCurrentYear()){
                if(month == getCurrentMonth()){
                    if(day == getCurrentDay()){
                        b = true;
                    }
                }
            }
        }
        return b;
    }

    // get with dash (-)
    public static String getWithDash(int i, int i1){
        return i+ DASH +getMonth(i1);
    }

    private static String getMonth(int month){
        String s = getString(month);
        if(month < 10){
            s = String.format("0%s",month);
        }
        return s;
    }

    private static String getDay(int day){
        String s = getString(day);
        if(day < 10){
            s = String.format("0%s",day);
        }
        return s;
    }

    // get with dash (-)
    public static String getWithDash(int i, int i1, int i2){
        return i+ DASH +getMonth(i1)+ DASH +getDay(i2);
    }

    // get the excel file
    public static String getExcelFileName(){
        return getFileName()+"ALL";
    }

    // get the excel file
    public static String getExcelFileName(boolean is_real_time){
        return "Export"+getCurrentMonth()+getRealTime(is_real_time)+"All"+getFileName();
    }

    // get the excel file
    private static String getRealTime(boolean is_real_time){
        String s = "";
        if(is_real_time){
            s = "Real_Time";
        }else{
            s = "Modify_Time";
        }
        return s;
    }

    // get excel file
    public static String getExcelFileName(String name){
        return getFileName()+name;
    }

    // get excel file
    public static String getExcelFileName(String name,boolean is_real_time){
        return "Export"+getCurrentMonth()+getRealTime(is_real_time)+name+getFileName();
    }

    // get file name in date time format yyyyMMddHHmmss
    private static String getFileName(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        return format.format(calendar.getTime());
    }

    // get current year from the calendar
    public static int getCurrentYear(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    // get current month from the calendar
    public static int getCurrentMonth(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH)+1;
    }

    // get current day from the calendar
    public static int getCurrentDay(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    // get year from the date given
    public static int getYearYM(String dateYM){
        if(dateYM.equals(""))return getCurrentYear();
        int i = getCurrentYear();
        String[] strings = dateYM.split(DASH);
        if(strings.length >= 2){
            i = convertToInt(strings[0]);
        }
        return i;
    }

    // get month from the date given
    public static int getMonthYM(String dateYM){
        if(dateYM.equals(""))return getCurrentMonth();
        int i = getCurrentMonth();
        String[] strings = dateYM.split(DASH);
        if(strings.length >= 2){
            i = convertToInt(strings[1]);
        }
        return i;
    }

    // get check status to show
    public static ArrayList<String> getCheckStatus(){
        ArrayList<String> list = new ArrayList<>();
        list.add(IN);
        list.add(OUT);
        return list;
    }

    // get check status to save to database
    public static String getStatusToSave(String text){
        String s = "";
        if(text.equals(IN))s = CHECK_IN;
        if(text.equals(OUT))s = CHECK_OUT;
        return s;
    }

    // get check status to show
    public static String getStatusToShow(String text){
        String s = "";
        if(text.equals(CHECK_IN))s = IN;
        if(text.equals(CHECK_OUT))s = OUT;
        return s;
    }

    // get string with colon (:)
    public static String getWithColon(int i, int i1){
        String h = getString(i);
        String m = getString(i1);
        if(i == 0){
            h = "00";
        }
        if(i1 == 0){
            m = "00";
        }
        return h+COLON+m;
    }

    // get the rule time for ex : 9:33 to 9:40
    public static String getRuleTime(TimeSheetItems items){
        if(items == null)return "";
        if(items.getCheckInTime().equals(""))return "";
        String[] strings = items.getCheckInTime().split(COLON);
        int hour = getInteger(strings[0]);
        int minute = getInteger(strings[1]);
        return getModifyTime(hour,minute,items.getCheckStatus());
    }

    // get the rule time for ex : 9:33 to 9:40
    public static String getRuleTime(String time,String checkStatus){
        if(time.equals(""))return time;
        int hour = getHour(time);
        int minute = getMinute(time);
        return getModifyTime(hour,minute,checkStatus);
    }

    // get modify time
    private static String getModifyTime(int hour, int minute,String check_status){
        String s = getFusionTime(hour,minute);
        if(check_status.equals(CHECK_IN)){
            if(hour <= 12){
                s = getMorningInTime(hour,minute);
            }else if(hour <= 17){
                s = getEveningInTime(hour, minute);
            }else {
                s = getEveningInTime(hour, minute);
            }
        }else {
            if(hour <= 12){
                s = getMorningOutTime(hour, minute);
            }else if(hour <= 18){
                s = getEveningOutTime(hour, minute);
            }else{
                s = getEveningOutTime(hour, minute);
            }
        }
        return s;
    }

    // get morning out time
    private static String getMorningOutTime(int hour, int minute){
        String s = "";
        if(hour == 12){
            if(minute <= 30){
                s = getFusionTime(hour);
            }
            else{
                s = getFusionTime(hour + 1);
            }
        }else{
            if(minute == 0){
                s = getFusionTime(hour);
            }else{
                if(minute >= 10){
                    s = getFusionTime(hour,10);
                    if(minute >= 20){
                        s = getFusionTime(hour,20);
                        if(minute >= 30){
                            s = getFusionTime(hour,30);
                            if(minute >= 40){
                                s = getFusionTime(hour,40);
                                if(minute >= 50){
                                    s = getFusionTime(hour,50);
                                }
                            }
                        }
                    }
                }
            }
        }
        return s;
    }

    // get morning in time
    private static String getMorningInTime(int hour, int minute){
        String s = "";
        if(minute == 0){
            s = getFusionTime(hour);
        }
        else if(minute <= 10){
            s = getFusionTime(hour,10);
        }
        else if(minute <= 20){
            s = getFusionTime(hour,20);
        }
        else if(minute <= 30){
            s = getFusionTime(hour,30);
        }
        else if(minute <= 40){
            s = getFusionTime(hour,40);
        }
        else if(minute <= 50){
            s = getFusionTime(hour,50);
        }
        else {
            s = getFusionTime((hour + 1));
        }
        return s;
    }

    // get evening out time
    private static String getEveningOutTime(int hour, int minute){
        String s = "";
        if(hour == 17) {
            if (minute == 0){
                s = getFusionTime(hour);
            }
            else if (minute >= 1){
                s = getFusionTime(hour);
            }
        }else{
            if(minute == 0){
                s = getFusionTime(hour);
            }else{
                if(minute >= 10){
                    s = getFusionTime(hour,10);
                    if(minute >= 20){
                        s = getFusionTime(hour,20);
                        if(minute >= 30){
                            s = getFusionTime(hour,30);
                            if(minute >= 40){
                                s = getFusionTime(hour,40);
                                if(minute >= 50){
                                    s = getFusionTime(hour,50);
                                }
                            }
                        }
                    }
                }
            }
        }
        return s;
    }

    // get evening in time
    private static String getEveningInTime(int hour, int minute){
        String s = "";
        if(minute == 0){
            s = getFusionTime(hour);
        }
        else if(minute <= 10){
            s = getFusionTime(hour,10);
        }
        else if(minute <= 20){
            s = getFusionTime(hour,20);
        }
        else if(minute <= 30){
            s = getFusionTime(hour,30);
        }
        else if(minute <= 40){
            s = getFusionTime(hour,40);
        }
        else if(minute <= 50){
            s = getFusionTime(hour,50);
        }
        else {
            s = getFusionTime((hour + 1));
        }
        return s;
    }

    // get fusion time with given parameter
    private static String getFusionTime(int hour,int minute){
        if(minute < 10){
            return hour+COLON+"0"+minute;
        }
        return hour+COLON+minute;
    }
    // get fusion time with given parameter
    private static String getFusionTime(String hour){
        if(hour.equals("0")){
            return "00"+ COLON+"00";
        }
        return hour+COLON+ "00";
    }
    // get fusion time with given parameter
    private static String getFusionTime(int hour){
        if(hour == 0){
            return "00"+ COLON+"00";
        }
        return hour+COLON+ "00";
    }

/*    // calculate time ------------------------------------>
    public static String getTotalTime(ArrayList<TimeSheetItems> timeSheetItems, int year, int month){
        ArrayList<String> dates = getDateList(year,month);
        int hour = 0;
        int minute = 0;
        for(int i = 0; i < dates.size(); i++){
            ArrayList<TimeSheetItems> list = getTimeSheetItemsWith(timeSheetItems,dates.get(i));
            String in = "";
            String out = "";
            for(int j = 0; j < list.size(); j++){
                TimeSheetItems items = list.get(j);
                if(items.getCheckStatus().equals(CHECK_IN)){
                    in = items.getCheckInTime();
                }
                if(items.getCheckStatus().equals(CHECK_OUT)){
                    out = items.getCheckInTime();
                }
                if(!in.equals("") && !out.equals("")){
                    long millisecond = getDurationTime(in,out);
                    int m = (int) ((millisecond / (1000*60)) % 60);
                    int h = (int) ((millisecond / (1000*60*60)) % 24);
                    hour = hour + Math.abs(h);
                    minute = minute + Math.abs(m);
                    in = "";
                    out = "";
                }
            }
        }
        return getCalculateTime(hour,minute);
    }*/

    // calculate time ------------------------------------>
    public static String getTotalTime(ArrayList<TimeSheetItems> timeSheetItems, int year, int month){
        ArrayList<String> dates = getDateList(year,month);
        int hour = 0;
        int minute = 0;
        for(int i = 0; i < dates.size(); i++){
            ArrayList<TimeSheetItems> list = getTimeSheetItemsWith(timeSheetItems,dates.get(i));
            String in = "";
            String out = "";
            for(int j = 0; j < list.size(); j++){
                TimeSheetItems items = list.get(j);
                if(items != null){
                    in = items.getCheckInTime();
                    out = items.getCheckOutTime();
                }
                if(!in.equals("") && !out.equals("")){
                    long millisecond = getDurationTime(in,out);
                    int m = (int) ((millisecond / (1000*60)) % 60);
                    int h = (int) ((millisecond / (1000*60*60)) % 24);
                    hour = hour + Math.abs(h);
                    minute = minute + Math.abs(m);
                    in = "";
                    out = "";
                }
            }
        }
        return getCalculateTime(hour,minute);
    }

    public static String millisecondToFullTime(long millisecond) {
        return timeUnitToFullTime(millisecond, TimeUnit.MILLISECONDS);
    }

    public static String secondToFullTime(long second) {
        return timeUnitToFullTime(second, TimeUnit.SECONDS);
    }

    @SuppressLint("DefaultLocale")
    public static String timeUnitToFullTime(long time, TimeUnit timeUnit) {
        //long day = timeUnit.toDays(time);
        long hour = timeUnit.toHours(time) % 24;
        long minute = timeUnit.toMinutes(time) % 60;
        //long second = timeUnit.toSeconds(time) % 60;
        if (hour > 0) {
            return String.format("%d:%02d", hour, minute);
        } else {
            return String.format("%02d", minute);
        }
    }


    @SuppressLint("SimpleDateFormat")
    public static long getDurationTime(String timeIn,String timeOut){
        if(timeIn.equals("") || timeOut.equals(""))return 0;
        Date time1 = null;
        try {
            time1 = new SimpleDateFormat("HH:mm").parse(timeIn);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(time1);

        Date time2 = null;
        try {
            time2 = new SimpleDateFormat("HH:mm").parse(timeOut);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(time2);

        if(calendar2.get(Calendar.AM_PM) == 1)     {
            if(calendar1.get(Calendar.AM_PM) == 0) {
                calendar2.add(Calendar.DATE, 1);
            }
        }
        return calendar1.getTimeInMillis() - calendar2.getTimeInMillis();
    }

    // get dates of the month of the year
    private static ArrayList<String> getDateList(int year, int month) {
        ArrayList<String> list = new ArrayList<>();
        Calendar start = Calendar.getInstance();
        start.set(MONTH, month - 1);  // month is 0 based on calendar
        start.set(YEAR, year);
        start.set(DAY_OF_MONTH, 1);
        start.getTime();
        start.set(DAY_OF_WEEK, SUNDAY);
        if (start.get(MONTH) <= (month - 1))
            start.add(DATE, -7);

        Calendar end = Calendar.getInstance();
        end.set(MONTH, month);  // next month
        end.set(YEAR, year);
        end.set(DAY_OF_MONTH, 1);
        end.getTime();
        end.set(DATE, -1);
        end.set(DAY_OF_WEEK, SATURDAY);
        start.getTime();
        if (end.get(MONTH) != month)
            end.add(DATE, +7);

        int i = 1;
        while (start.before(end)) {
            if(currentMonth(printCalendar(start),month)){
                list.add(printCalendar(start));
            }
            if (i % 7 == 0) {   // last day of the week
                System.out.println();
                i  = 1;
            } else {
                i++;
            }
            start.add(DATE, 1);
        }
        return list;
    }

    // date format
    @SuppressLint("SimpleDateFormat")
    private final static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    // get calender date time
    private static String printCalendar(Calendar c) {
        return df.format(c.getTime());
    }

    // check date is current month
    private static boolean currentMonth(String date, int month){
        if(date.equals(""))return false;
        boolean b = false;
        String[] strings = date.split(DataManager.DASH);
        int m = DataManager.convertToInt(strings[1]);
        if(m == month){
            b = true;
        }
        return b;
    }

    // <------------------------------------------------------------------

    // check email format
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // check the admin signIn
    public static void onAdminSignIn(String email,String password,SignInResult result){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                result.onSuccess(auth.getCurrentUser());
            }else{
                result.onFail(task.toString());
            }
        });
    }

    // get hour from time
    public static int getHour(String time){
        if(time.equals(""))return 0;
        String[] strings = time.split(COLON);
        return getInteger(strings[0]);
    }

    // get minute from time
    public static int getMinute(String time){
        if(time.equals(""))return 0;
        String[] strings = time.split(COLON);
        return getInteger(strings[1]);
    }

    // get all time in month
    public static String getAllTimeInMonth(int year, int month){
        ArrayList<String> dates = getDateList(year,month);
        int hour = 0;
        int minute = 0;
        Calendar calendar = Calendar.getInstance();
        for(int i = 0; i < dates.size(); i++){
            Date date = getDate(dates.get(i));
            if(date != null){
                calendar.setTime(date);
                int day = calendar.get(DAY_OF_WEEK);
                // 1 for sunday
                // 7 for saturday
                if(day != 7 && day != 1){
                    long mil = getDurationTime("08:30", "12:00");
                    long mil1 = getDurationTime("13:00","17:00");
                    int minutes1 = (int) ((mil / (1000*60)) % 60);
                    int hours1 = (int) ((mil / (1000*60*60)) % 24);
                    int minutes2 = (int) ((mil1 / (1000*60)) % 60);
                    int hours2 = (int) ((mil1 / (1000*60*60)) % 24);
                    hour = hour + Math.abs(hours1) + Math.abs(hours2);
                    minute = minute + Math.abs(minutes1) + Math.abs(minutes2);
                }
            }
        }
        return getCalculateTime(hour,minute);
    }

    // calculate time
    private static String getCalculateTime(int hour, int minute){
        if(minute == 0)return getFusionTime(hour);
        int h = minute / 60;
        int m = minute % 60;
        if(h > 0){
            h = hour + h;
        }else{
            h = hour;
        }
        return getFusionTime(h,m);
    }

    // get date
    private static Date getDate(String string){
        if(string.equals(""))return null;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format.parse(string);
        } catch (ParseException e) {
            return null;
        }
    }

    // get all lost time in month
    public static String getAllLostTimeInMonth(String total_time_in_month, String all_time_in_month){
        if(total_time_in_month.equals("") || all_time_in_month.equals(""))return "00:00";
        String[] totals = total_time_in_month.split(COLON);
        String[] all_times = all_time_in_month.split(COLON);
        int h = 0;
        int m = 0;
        int h1 = getInteger(totals[0]);
        int h2 = getInteger(all_times[0]);
        int m1 = getInteger(totals[1]);
        int m2 = getInteger(all_times[1]);
        int minuteTotal = (h1 * 60) + m1;
        int minuteAll = (h2 * 60) + m2;
        int minute = minuteAll - minuteTotal;
        h = minute / 60;
        m = minute % 60;
        return getFusionTime(h,m);
    }

    // get default check out time
    public static String getDefaultCheckOutTime(String time){
        if(time.equals(""))return time;
        String s = "";
        int hour = getHour(time);
        if(hour <= 11){
            s = DataManager.Part.getMorningPart().getTimeOut();
        }else {
            s = DataManager.Part.getAfterNoonPart().getTimeOut();
        }
        return s;
    }

    // get id
    public static String getId(){
        return getFileName();
    }

    // check is nearly in work time
    public static boolean IsNearlyIn(String current_time,TimeSheetItems items){
        if(current_time.equals(""))return false;
        if(items.getCheckOutTime().equals(""))return false;
        boolean b = false;
        int cur_hour = getHour(current_time);
        int cur_minute = getMinute(current_time);
        int out_hour = getHour(items.getCheckOutTime());
        if(out_hour <= 12){
            if(cur_hour >= 12 ){
                if(cur_minute >= 30){
                    b = true;
                }
                if(cur_hour > 13){
                    b = true;
                }
            }
        }
        return b;
    }

    // check if is over time
    public static boolean IsOverTime(String currentTime){
        if(currentTime.equals("")) return false;
        boolean b = false;
        int hour = getHour(currentTime);
        int minute = getMinute(currentTime);
        if(hour >= 17){
            if(minute >= 30){
               b = true;
            }
        }
        return b;
    }

    // get name from list of user by id
    public static String getUserNameById(ArrayList<UserItems> userItems,String userId){
        String s = "";
        if(userItems != null){
            for (int i = 0; i < userItems.size(); i++){
                UserItems items = userItems.get(i);
                if(items.getUserId().equals(userId)){
                    s = items.getName();
                }
            }
        }
        return s;
    }
}
