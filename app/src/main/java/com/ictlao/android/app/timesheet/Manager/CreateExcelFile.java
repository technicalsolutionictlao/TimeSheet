package com.ictlao.android.app.timesheet.Manager;

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.YEAR;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.aspose.cells.BackgroundType;
import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Color;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.Style;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ictlao.android.app.timesheet.Items.ExcelItems;
import com.ictlao.android.app.timesheet.Items.TimeSheetItems;
import com.ictlao.android.app.timesheet.Items.UserItems;
import com.ictlao.android.app.timesheet.Items.VacationItems;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class CreateExcelFile {

    // response message from this class
    public static final String Failed = "Failed";
    public static final String Success = "Success";

    // declare alphabets for cell name
    private static final String A = "A";
    private static final String B = "B";
    private static final String C = "C";
    private static final String D = "D";
    private static final String E = "E";
    private static final String F = "F";
    private static final String G = "G";
    private static final String H = "H";
    private static final String I = "I";
    private static final String J = "J";
    private static final String K = "K";
    private static final String L = "L";
    private static final String M = "M";
    private static final String N = "N";
    private static final String O = "O";
    private static final String P = "P";
    private static final String Q = "Q";
    private static final String R = "R";
    private static final String S = "S";
    private static final String T = "T";
    private static final String U = "U";
    private static final String V = "V";
    private static final String W = "W";
    private static final String X = "X";
    private static final String Y = "Y";
    private static final String Z = "Z";

    // path for store the path to show the user
    private String mPath = "";

    // the activity that user this class
    private Activity mActivity = null;

    // alphabet list
    private ArrayList<String> getAlphabetList(){
        ArrayList<String> list = new ArrayList<>();
        list.add(B);
        list.add(C);
        list.add(D);
        list.add(E);
        list.add(F);
        list.add(G);
        list.add(H);
        list.add(I);
        list.add(J);
        list.add(K);
        list.add(L);
        list.add(M);
        list.add(N);
        list.add(O);
        list.add(P);
        list.add(Q);
        list.add(R);
        list.add(S);
        list.add(T);
        list.add(U);
        list.add(V);
        list.add(W);
        list.add(X);
        list.add(Y);
        list.add(Z);
        return list;
    }

    // alphabet list
    private ArrayList<String> getAlphabetListContainA(){
        ArrayList<String> list = new ArrayList<>();
        list.add(A);
        list.add(B);
        list.add(C);
        list.add(D);
        list.add(E);
        list.add(F);
        list.add(G);
        list.add(H);
        list.add(I);
        list.add(J);
        list.add(K);
        list.add(L);
        list.add(M);
        list.add(N);
        list.add(O);
        list.add(P);
        list.add(Q);
        list.add(R);
        list.add(S);
        list.add(T);
        list.add(U);
        list.add(V);
        list.add(W);
        list.add(X);
        list.add(Y);
        list.add(Z);
        return list;
    }

    // alphabet list
    private ArrayList<String> getAlphabetListContainAList(){
        ArrayList<String> list = new ArrayList<>();
        list.add(A);
        list.add(B);
        list.add(C);
        list.add(D);
        list.add(E);
        list.add(F);
        list.add(G);
        list.add(H);
        list.add(I);
        list.add(J);
        list.add(K);
        list.add(L);
        list.add(M);
        list.add(N);
        list.add(O);
        list.add(P);
        list.add(Q);
        list.add(R);
        list.add(S);
        list.add(T);
        list.add(U);
        list.add(V);
        list.add(W);
        list.add(X);
        list.add(Y);
        list.add(Z);
        for(int i = 0; i < getAlphabetListContainA().size(); i++){
            list.add(A+getAlphabetListContainA().get(i));
        }
        return list;
    }

    // Constructor for this class
    public CreateExcelFile(Activity activity){
        mActivity = activity;
    }

    // get path : emulated/0/TimeSheet
    private String getPath(){
        return Environment.getExternalStorageDirectory()+"/TimeSheet";
    }

    // get path : emulated/0/document/TimeSheet
    private String getDownloadDirectory(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/TimeSheet";
    }

    // get path : emulated/0/data/application package/data/TimeSheet
    private String getApplicationDirectory(){
        return mActivity.getExternalFilesDir("TimeSheet").getAbsolutePath();
    }

    // create excel file for total time of the users
    public String onCreateTotalTime(ArrayList<UserItems> userItems,
                           ArrayList<TimeSheetItems> timeSheetItems,
                           ArrayList<VacationItems> vacationItems,
                           int year,int month,String fileName){
        ArrayList<String> dates = getDateList(year, month);

        String message = "";
        // create style and set title of excel file  ---->
        Workbook workbook = new Workbook();
        Worksheet sheet = workbook.getWorksheets().get(0);
        Cells cells = sheet.getCells();
        Cell cell = cells.get(A+1);
        cell.setValue(String.format("%s","ຕາຕະລາງເຂົ້າວຽກ ພະນັກງານ "+dates.get(0)+" "+DataManager.DASH+" "+dates.get(dates.size() - 1)));
        Style style = cell.getStyle();
        //style.setPattern(BackgroundType.SOLID);
        //style.setForegroundColor(Color.getBlue());
        style.getFont().setColor(Color.getBlue());
        style.getFont().setBold(true);
        cell.setStyle(style);
        // <------------------------------------------

        // set title of the excel content -------------------->
        ArrayList<String> titles = new ArrayList<>();
        titles.add(String.format("%s","ຊື່"));
        titles.add(String.format("%s","ເວລາເຮັດວຽກທັງໝົດ"));
        titles.add(String.format("%s","ເວລາເຮັດວຽກຕົວຈິງ"));
        titles.add(String.format("%s","ເວລາຂາດ"));
        titles.add(String.format("%s","ໝາຍເຫດ"));

        for(int i = 0; i < titles.size(); i++){
            cell = cells.get(getAlphabetListContainA().get(i)+2);
            cell.setValue(titles.get(i));
        }
        // <---------------------------------------------------

        int index = 3;

        for(int i = 0; i < userItems.size(); i++){
            cell = cells.get(A+index);
            cell.setValue(userItems.get(i).getName());
            ArrayList<TimeSheetItems> timeSheetItems1 = getTimeSheetItems(timeSheetItems,userItems.get(i).getUserId());
            ArrayList<VacationItems> vacationItems1 = getVacationItems(vacationItems,userItems.get(i).getUserId());

            String all_time = DataManager.getAllTimeInMonth(year,month);
            String total_time = DataManager.getTotalTime(timeSheetItems1,year,month);
            String lost_time = DataManager.getAllLostTimeInMonth(total_time,all_time);

            // set all time in month
            cell = cells.get(B+index);
            cell.setValue(all_time);

            // set total time in month
            cell = cells.get(C+index);
            cell.setValue(total_time);

            // set lost time in month
            cell = cells.get(D+index);
            cell.setValue(lost_time);

            // set vacations items
            for(int j = 0; j < vacationItems1.size(); j++){
                VacationItems items = vacationItems1.get(j);
                cell = cells.get(E+index);
                cell.setValue(String.format("%s","ລາພັກ ເງື່ອນໄຂ : "+items.getUseVacationCase()+" ຈໍານວນ : "+items.getDay_qty()+" ມື້. ເຫດຜົນ : "+items.getRemark()));
                index++;
            }
            index++;
        }

        // create file with the content above
        String path = getDisplayPath();
        File directory = new File(path);
        if (!directory.exists()){
            directory.mkdir();
        }
        path = path+"/"+fileName+".xlsx";
        try {
            workbook.getSettings().getWriteProtection().setRecommendReadOnly(true);
            workbook.save(path, FileFormatType.XLSX);
            message = Success;
            workbook.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            message = Failed;
        }
        return message;
    }

    // create excel file for timesheet of the users
    public String onCreate(ArrayList<TimeSheetItems> timeSheetItems,
                            ArrayList<UserItems> userItems,String fileName,
                            int year, int month, ArrayList<VacationItems> vacationItems){
        ArrayList<String> dates = getDateList(year, month);
        ArrayList<String> alphabetAList = getAlphabetListContainAList();

        String message = "";
        // create style and set title of excel file  ---->
        Workbook workbook = new Workbook();
        Worksheet sheet = workbook.getWorksheets().get(0);
        Cells cells = sheet.getCells();
        String title = String.format("ຕາຕະລາງເຂົ້າວຽກ ພະນັກງານ %s - %s",dates.get(0),dates.get(dates.size() - 1));
        Cell cell = cells.get(A+1);
        cell.setValue(title);
        Style style = cell.getStyle();
        //style.setPattern(BackgroundType.SOLID);
        //style.setForegroundColor(Color.getBlue());
        style.getFont().setColor(Color.getBlue());
        style.getFont().setBold(true);
        cell.setStyle(style);
        // <------------------------------------------

        // set date of the month in the row 2 in of the file --->
        for(int j = 0; j < dates.size(); j++){
            cell = cells.get(alphabetAList.get(j)+2);
            cell.setValue(getDay(dates.get(j)));
        }
        // <---------------------------------------------------

        // set data from time sheet list to the file ---------->
        int index = 3;

        ArrayList<HashMap<String, String>> map = getDateNumberValues(cells);

        for(int i = 0; i < userItems.size(); i++){
            cell = cells.get(A+index);
            cell.setValue(userItems.get(i).getName());
            cell.getStyle().getFont().setBold(true);

            ArrayList<ExcelItems> excelItems = getExcelItems(timeSheetItems,
                    userItems.get(i).getUserId(),
                    userItems.get(i).getName());
            ArrayList<VacationItems> vacationItemsList = getVacationItems(vacationItems,userItems.get(i).getUserId());
            index++;
            ArrayList<Integer> indexes = new ArrayList<>();
            for(int j = 0; j < dates.size(); j++){
                ArrayList<ExcelItems> list = getExcelItems(dates.get(j),excelItems);
                ArrayList<VacationItems> vacationItemsCurrentDate = getVacationItems(dates.get(j), vacationItemsList);
                int row = index;
                for(int l = 0; l < list.size(); l++){
                    ExcelItems items = list.get(l);
                    for(int m = 0; m < map.size(); m++) {
                        if(map.get(m).containsKey(getDay(items.getDate()))){
                            // check in time
                            cell = cells.get(map.get(m).get(getDay(items.getDate()))+ row);
                            cell.setValue(items.getCheckInTime());
                            Style s = cell.getStyle();
                            s.setCustom("#");
                            //s.setForegroundColor(Color.getTransparent());
                            s.getFont().setColor(Color.getBlack());
                            if(!items.getDescription().equals("")){
                                s.setPattern(BackgroundType.SOLID);
                                s.setForegroundColor(Color.getGreen());
                            }
                            cell.setStyle(s);
                            // check out time
                            row++;
                            cell = cells.get(map.get(m).get(getDay(items.getDate()))+row);
                            cell.setValue(items.getCheckOutTime());
                            if(items.getCheck_status().equals(DataManager.CHECK_IN)){
                                s.setPattern(BackgroundType.SOLID);
                                s.setForegroundColor(Color.getRed());
                            }
                            cell.setStyle(s);
                            row++;
                        }
                    }
                }

                // set the color of vacation cell to Yellow color
                for(int n = 0; n < vacationItemsCurrentDate.size(); n++){
                    VacationItems items = vacationItemsCurrentDate.get(n);
                    for(int o = 0; o < map.size(); o++){
                        if(map.get(o).containsKey(getDay(items.getDate()))){
                            cell = cells.get(map.get(o).get(getDay(items.getDate()))+ row);
                            cell.setValue("");
                            Style s = cell.getStyle();
                            s.setPattern(BackgroundType.SOLID);
                            s.setForegroundColor(Color.getYellow());
                            s.getFont().setColor(Color.getBlack());
                            cell.setStyle(s);
                            row++;
                        }
                    }
                }

                if(!indexes.contains(row)) {
                    indexes.add(row);
                }
            }
            index = getIndex(indexes);
        }
        // <--------------------------------------------------------------

        // set ໝາຍເຫດ of the vacation or holiday -------------->
        index++;
        cell = cells.get(A+index);
        cell.setValue(String.format("%s","ໝາຍເຫດ"));
        cell.setStyle(style);
        index++;

        for(int x = 0; x < vacationItems.size(); x++){
            VacationItems items = vacationItems.get(x);
            cell = cells.get(A+index);
            cell.setValue(String.format("%s",items.getName()+" ວັນທີອະນຸຍາດ "+items.getDate()+" ລາພັກ ເງື່ອນໄຂ : "+items.getUseVacationCase()+" ຈໍານວນ : "+items.getDay_qty()+" ມື້. ເຫດຜົນ : "+items.getRemark() +" ເລີ່ມວັນທີ "+items.getStart_date()+" - "+items.getEnd_date()));
            index++;
        }
        // <-----------------------------------------------------

        // set description for each person in -------------------->
        index++;
        for(int i = 0; i < timeSheetItems.size(); i++){
            TimeSheetItems items = timeSheetItems.get(i);
            if(!items.getDescription().equals(DataManager.AUTO_SET)) {
                if(!items.getDescription().trim().equals("")) {
                    cell = cells.get(A + index);
                    String name = DataManager.getUserNameById(userItems, items.getUserId());
                    String value = name +" " +items.getDate()+ " ເຫດຜົນ " + items.getDescription();
                    cell.setValue(String.format("%s", value));
                    index++;
                }
            }
        }
        // <------------------------------------------------------

        // color information ----------------------------------->
        // blue is tile.
        // red is user forgot to check out.
        // yellow is user on vacation days.
        index++;
        // red cell
        cell = cells.get(A+index);
        Style redStyle = cell.getStyle();
        redStyle.setPattern(BackgroundType.SOLID);
        redStyle.setForegroundColor(Color.getRed());
        cell.setStyle(redStyle);
        cell = cells.get(B+index);
        cell.setValue(String.format("%s","ເພີ່ມອັດຕະໂນມັດ ໂດຍໂປຣແກຣມ"));
        index++;
        // yellow cell
        cell = cells.get(A+index);
        Style yellowStyle = cell.getStyle();
        yellowStyle.setPattern(BackgroundType.SOLID);
        yellowStyle.setForegroundColor(Color.getYellow());
        cell.setStyle(yellowStyle);
        cell = cells.get(B+index);
        cell.setValue(String.format("%s","ພະນັກງານ ລາພັກ"));
        index++;
        // blue cell
        /*cell = cells.get(A+index);
        Style blueStyle = cell.getStyle();
        blueStyle.setPattern(BackgroundType.SOLID);
        blueStyle.setForegroundColor(Color.getBlue());
        cell.setStyle(blueStyle);
        cell = cells.get(B+index);
        cell.setValue(String.format("%s","ຫົວຂໍ້"));*/

        // green cell
        //index++;
        cell = cells.get(A+index);
        Style greenStyle = cell.getStyle();
        greenStyle.setPattern(BackgroundType.SOLID);
        greenStyle.setForegroundColor(Color.getGreen());
        cell.setStyle(greenStyle);
        cell = cells.get(B+index);
        cell.setValue(String.format("%s","ເຫດຜົນໃນການເຂົ້າວຽກ"));
        // <-----------------------------------------------------

        // create file with the content above
        String path = getDisplayPath();
        File directory = new File(path);
        if (!directory.exists()){
            directory.mkdir();
        }
        path = path+"/"+fileName+".xlsx";
        try {
            workbook.getSettings().getWriteProtection().setRecommendReadOnly(true);
            workbook.save(path, FileFormatType.XLSX);
            message = Success;
        } catch (Exception e) {
            e.printStackTrace();
            message = Failed;
        }
        return message;
    }

    // get dates of the month of the year
    private ArrayList<String> getDateList(int year, int month) {
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

    // date formate
    @SuppressLint("SimpleDateFormat")
    private final static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    // get calender date time
    private String printCalendar(Calendar c) {
        return df.format(c.getTime());
    }

    // check date is current month
    private boolean currentMonth(String date, int month){
        if(date.equals(""))return false;
        boolean b = false;
        String[] strings = date.split(DataManager.DASH);
        int m = DataManager.convertToInt(strings[1]);
        if(m == month){
            b = true;
        }
        return b;
    }

    // getExcel file from the timesheet
    private ArrayList<ExcelItems> getExcelItems(ArrayList<TimeSheetItems> timeSheetItems, String userId, String name){
        ArrayList<ExcelItems> list = new ArrayList<>();
        for(int i = 0; i < timeSheetItems.size(); i++){
            TimeSheetItems items = timeSheetItems.get(i);
            if(userId.equals(items.getUserId())){
                ExcelItems excelItems = new ExcelItems(items.getUserId(),name,items.getDate(),items.getCheckInTime(),
                        items.getCheckStatus(),items.getTimeInMarker(),items.getDescription(),items.getCheckOutTime(),
                        items.getTimeOutMarker());
                list.add(excelItems);
            }
        }
        return list;
    }

    private ArrayList<TimeSheetItems> getTimeSheetItems(ArrayList<TimeSheetItems> timeSheetItems,String userId){
        ArrayList<TimeSheetItems> list = new ArrayList<>();
        for(int i = 0; i < timeSheetItems.size(); i++){
            TimeSheetItems items = timeSheetItems.get(i);
            if(items.getUserId().equals(userId)){
                list.add(items);
            }
        }
        return list;
    }

    // get vacations items of the user id
    private ArrayList<VacationItems> getVacationItems(ArrayList<VacationItems> vacationItems, String userId){
        ArrayList<VacationItems> list = new ArrayList<>();
        for(int i = 0; i < vacationItems.size(); i++){
            VacationItems items = vacationItems.get(i);
            if(userId.equals(items.getUserId())){
                list.add(items);
            }
        }
        return list;
    }

    // get vacation items from the date
    private ArrayList<VacationItems> getVacationItems(String date,ArrayList<VacationItems> vacationItems){
        ArrayList<VacationItems> list = new ArrayList<>();
        for(int i = 0; i < vacationItems.size(); i++){
            VacationItems items = vacationItems.get(i);
            if(date.equals(items.getDate())){
                list.add(items);
            }
        }
        return list;
    }

    // get excel items from the date
    private ArrayList<ExcelItems> getExcelItems(String date, ArrayList<ExcelItems> excelItems){
        ArrayList<ExcelItems> list = new ArrayList<>();
        for(int i = 0; i < excelItems.size(); i++){
            ExcelItems items = excelItems.get(i);
            if(date.equals(items.getDate())){
                list.add(items);
            }
        }
        return list;
    }

    // create folder path
    public boolean onCreatePath(){
        boolean mkdir = false;
        mPath = getPath();
        File directory = new File(mPath);
        if (! directory.exists()){
            mkdir = directory.mkdir();
        }

        if(directory.exists()){
            mkdir = true;
        }

        if(!mkdir){
            mPath = getDownloadDirectory();
            File file = new File(mPath);
            if(!file.exists()){
                mkdir = file.mkdir();
            }else{
                mkdir = true;
            }
        }

        if(!mkdir){
            mPath = getApplicationDirectory();
            File file = new File(mPath);
            if(!file.exists()){
                mkdir = file.mkdir();
            }else {
                mkdir = true;
            }
        }
        return mkdir;
    }

    // display the file path to users
    public String getDisplayPath(){
        return mPath;
    }

    // get day from the date
    private String getDay(String date){
        if(date.equals(""))return date;
        String[] strings = date.split(DataManager.DASH);
        return strings[2];
    }

    // get date number from row 2 in the file with alphabet
    private ArrayList<HashMap<String, String>> getDateNumberValues(Cells cells){
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        for(int i = 0; i < (cells.getMaxDataColumn() + 1); i++){
            String value = cells.get(getAlphabetListContainAList().get(i)+2).getStringValue();
            HashMap<String,String> map = new HashMap<>();
            if(!value.equals("")){
                map.put(value,getAlphabetListContainAList().get(i));
                list.add(map);
            }
        }
        return list;
    }

    // get max index number
    private int getIndex(ArrayList<Integer> list){
        int i = 0;
        for (int j = 0; j < list.size(); j++){
            if(i < list.get(j)){
                i = list.get(j);
            }
        }
        return i + 1;
    }

    //https://stackoverflow.com/questions/40741247/package-folder-not-created-android/40741608
}
