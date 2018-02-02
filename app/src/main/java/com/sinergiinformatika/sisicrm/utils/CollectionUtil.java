package com.sinergiinformatika.sisicrm.utils;

import android.content.Context;
import android.util.Log;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.data.models.ItemHeader;
import com.sinergiinformatika.sisicrm.data.models.Store;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by wendi on 30-Dec-14.
 */
public class CollectionUtil {

    private static final String TAG = CollectionUtil.class.getSimpleName();

    public static List<ItemHeader<Store>> groupStoreListByPriority(final List<Store> stores) {
        if (stores == null || stores.size() == 0) {
            return null;
        }

        List<ItemHeader<Store>> result = new ArrayList<>();

        Map<Integer, List<Store>> map = new TreeMap<>();

        for (Store store : stores) {

            if (!Constants.STORE_STATUS_ACTIVE.equals(store.getStatus())) {
                continue;
            }

            Store s = null;
            try {
                s = (Store) store.clone();
            } catch (CloneNotSupportedException e) {
                Log.e(TAG, e.getMessage(), e);
            }

            if (s == null) {
                continue;
            }

            Integer key = s.getCategoryCode();

            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<Store>());
            }

            List<Store> storesGroup = map.get(key);
            storesGroup.add(s);

            //update value map
            map.put(key, storesGroup);
        }

        for (Integer key : map.keySet()) {

            //Log.d("CollectionUtil.groupStoreListByPriority", "key = "+key);

            ItemHeader<Store> itemHeader = new ItemHeader<>();

            if (Constants.STORE_CATEGORY_CODE_PLATINUM == key) {
                //Resources.getSystem().getString(R.string.label_platinum)
                itemHeader.setHeaderName("Platinum");
            } else if (Constants.STORE_CATEGORY_CODE_GOLD == key) {
                //Resources.getSystem().getString(R.string.label_gold)
                itemHeader.setHeaderName("Gold");
            } else if (Constants.STORE_CATEGORY_CODE_SILVER == key) {
                //Resources.getSystem().getString(R.string.label_silver)
                itemHeader.setHeaderName("Silver");
            }

            itemHeader.setItems(map.get(key));

            result.add(itemHeader);
        }

        return result;

    }

    public static List<ItemHeader<Store>> groupStoreListByLastCheckIn(final List<Store> stores) {

        if (stores == null || stores.size() == 0) {
            return null;
        }

        List<ItemHeader<Store>> result = new ArrayList<>();

        Map<String, List<Store>> map = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return rhs.compareTo(lhs);
            }
        });

        for (Store store : stores) {

            if (!Constants.STORE_STATUS_ACTIVE.equals(store.getStatus())) {
                continue;
            }

            Store s = null;
            try {
                s = (Store) store.clone();
            } catch (CloneNotSupportedException e) {
                Log.e(TAG, e.getMessage(), e);
            }

            if (s == null) {
                continue;
            }

            String key = "-";
            Date lastCheckIn = s.getLastCheckInDate();
            if (lastCheckIn != null) {
                SimpleDateFormat weekFormat = new SimpleDateFormat("W");
                String weekNumInStr = weekFormat.format(lastCheckIn);
                int weekNum = Integer.parseInt(weekNumInStr);
                int thisWeekNum = Integer.parseInt(weekFormat.format(Calendar.getInstance().getTime()));
                int weekDiff = thisWeekNum - weekNum;

                if (weekDiff <= 4) {
                    key = "Sebulan yang lalu";
                } /*else if (weekDiff <= 8) {
                    key = "Dua bulan yang lalu";
                } */ else {
                    key = "Lebih dari sebulan";
                }

                switch (weekDiff) {
                    case 0:
                        key = "Pekan ini";
                        break;
                    case 1:
                        key = "Sepekan yang lalu";
                        break;
                    case 2:
                        key = "Dua pekan yang lalu";
                }
            }
            /*String key = s.getLastCheckInYYYYMMDD();
            if(key == null){
                key = "-";
            }*/

            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<Store>());
            }

            List<Store> storesGroup = map.get(key);
            storesGroup.add(s);

            //update value map
            map.put(key, storesGroup);
        }

        for (String key : map.keySet()) {

            String headerName = "-";
            if (key != null && key.trim().length() > 0) {
                if ("-".equals(key.trim())) {
                    headerName = "Belum pernah disurvey";
                } else {
                    headerName = key;
                }
            }
            /*if(key != null && key.trim().length() > 0 && !"-".equals(key.trim())) {
                try {
                    headerName = DateUtil.format(DateUtil.parse(key), "dd MMMM yyyy");
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }*/

            ItemHeader<Store> itemHeader = new ItemHeader<>();
            itemHeader.setHeaderName(headerName);
            itemHeader.setItems(map.get(key));
            result.add(itemHeader);
        }

        return result;

    }

    public static List<ItemHeader<Store>> groupStoreListByCreateDate(Context context,
                                                                     final List<Store> stores) {

        if (stores == null || stores.size() == 0) {
            return null;
        }

        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        int thisWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        List<ItemHeader<Store>> result = new ArrayList<>();

        Map<Integer, List<Store>> map = new TreeMap<>();

        for (Store store : stores) {

            if (!Constants.STORE_STATUS_ACTIVE.equals(store.getStatus())) {
                continue;
            }

            Store s = null;
            try {
                s = (Store) store.clone();
            } catch (CloneNotSupportedException e) {
                Log.e(TAG, e.getMessage(), e);
            }

            if (s == null) {
                continue;
            }

            int key = 9;
            Date createDate = DateUtil.parse(s.getCreated(), false);
            if (createDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(createDate);
                int createYear = cal.get(Calendar.YEAR);
                int createWeek = cal.get(Calendar.WEEK_OF_YEAR);
                int weekDiff;

                if (createYear == thisYear) {
                    weekDiff = thisWeek - createWeek;
                } else {
                    cal.set(Calendar.MONTH, Calendar.DECEMBER);
                    cal.set(Calendar.DATE, 31);
//                    int createMaxWeek = cal.get(Calendar.WEEK_OF_YEAR);
                    int createMaxWeek = cal.getActualMaximum(Calendar.WEEK_OF_YEAR);
                    weekDiff = (createMaxWeek + thisWeek) - createWeek;
                }

                if (weekDiff > 4) {
                    key = 5;
                } else {
                    key = weekDiff;
                }
            }

            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<Store>());
            }

            List<Store> storesGroup = map.get(key);
            storesGroup.add(s);

            //update value map
            map.put(key, storesGroup);
        }

        for (Integer key : map.keySet()) {

            String headerName = context.getString(R.string.label_create_date_unknown);
            if (key != null && key >= 0) {
                switch (key) {
                    case 0:
                        headerName = context.getString(R.string.label_this_week);
                        break;
                    case 1:
                        headerName = context.getString(R.string.label_last_week);
                        break;
                    case 2:
                        headerName = context.getString(R.string.label_two_weeks_ago);
                        break;
                    case 3:
                    case 4:
                        headerName = context.getString(R.string.label_one_month_ago);
                        break;
                    case 5:
                        headerName = context.getString(R.string.label_more_than_one_month_ago);
                        break;
                }
            }

            ItemHeader<Store> itemHeader = new ItemHeader<>();
            itemHeader.setHeaderName(headerName);
            itemHeader.setItems(map.get(key));
            result.add(itemHeader);
        }

        return result;

    }

    public static List<ItemHeader<Store>> groupStoreListByDistance(final List<Store> stores, final double currentLatitude, final double currentLongitude) {

        if (stores == null || stores.size() == 0) {
            return null;
        }

        List<ItemHeader<Store>> result = new ArrayList<>();

        Map<Integer, List<Store>> map = new TreeMap<>();

        for (Store store : stores) {

            if (!Constants.STORE_STATUS_ACTIVE.equals(store.getStatus())) {
                continue;
            }

            Store s = null;
            try {
                s = (Store) store.clone();
            } catch (CloneNotSupportedException e) {
                Log.e(TAG, e.getMessage(), e);
            }

            if (s == null) {
                continue;
            }

            Long distance = (long) 1001;

            if (currentLatitude != 0.0 && currentLongitude != 0.0) {
                distance = s.getStoreDistanceInLong(currentLatitude, currentLongitude);
                if (distance == null) {
                    distance = (long) 1001;
                }
            }

            //Log.d(TAG, "distance = " + distance);

            Integer key;

            if (distance.intValue() <= 100) {
                key = 100;
            } else if (distance.intValue() > 100 && distance.intValue() <= 500) {
                key = 500;
            } else if (distance.intValue() > 500 && distance.intValue() <= 1000) {
                key = 1000;
            } else {
                key = 1001;//lebih dari 1 KM
            }

            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<Store>());
            }

            List<Store> storesGroup = map.get(key);
            storesGroup.add(s);

            //update value map
            map.put(key, storesGroup);
        }

        for (Integer key : map.keySet()) {

            //Log.e(TAG, "key = "+key);

            String headerName;
            if (key <= 100) {
                headerName = "100m";
            } else if (key > 100 && key <= 500) {
                headerName = "500m";
            } else if (key > 500 && key <= 1000) {
                headerName = "1km";
            } else {
                headerName = "Lebih dari 1km";//lebih dari 1 KM
            }

            //Log.e(TAG, "headerName = "+headerName);


            ItemHeader<Store> itemHeader = new ItemHeader<>();
            itemHeader.setHeaderName(headerName);

            List<Store> stores2 = map.get(key);

            Collections.sort(stores2, new Comparator<Store>() {
                @Override
                public int compare(Store lhs, Store rhs) {
                    Long d1 = lhs.getStoreDistanceInLong(currentLatitude, currentLongitude);
                    Long d2 = rhs.getStoreDistanceInLong(currentLatitude, currentLongitude);

                    if (d1 != null && d2 != null) {
                        return d1.compareTo(d2);
                    }

                    if (d1 != null)
                        return 1;

                    if (d2 != null)
                        return -1;

                    return 0;
                }
            });

            itemHeader.setItems(stores2);
            result.add(itemHeader);
        }

        return result;

    }


    //TODO perlu di perbaiki, method nya mirip groupStoreListByDistance()

    /**
     * get new store, status = unverified or verified
     *
     * @param stores           list of Store
     * @param currentLatitude  latitude of current position
     * @param currentLongitude longitude of current position
     * @return list of newly added Store
     */
    public static List<ItemHeader<Store>> populateNewStores(final List<Store> stores,
                                                            final double currentLatitude,
                                                            final double currentLongitude) {

        if (stores == null || stores.size() == 0) {
            return null;
        }

        List<ItemHeader<Store>> result = new ArrayList<>();

        Map<Integer, List<Store>> map = new TreeMap<>();

        for (Store store : stores) {

            if (Constants.STORE_STATUS_ACTIVE.equals(store.getStatus())) {
                continue;
            }

            Store s = null;
            try {
                s = (Store) store.clone();
            } catch (CloneNotSupportedException e) {
                Log.e(TAG, e.getMessage(), e);
            }

            if (s == null) {
                continue;
            }

            Integer key = 0;

            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<Store>());
            }

            List<Store> storesGroup = map.get(key);
            storesGroup.add(s);

            //update value map
            map.put(key, storesGroup);
        }

        for (Integer key : map.keySet()) {

            //TODO label masih di-harcode
            String headerName;
            if (key <= 100) {
                headerName = "100m";
            } else if (key > 100 && key <= 500) {
                headerName = "500m";
            } else if (key > 500 && key <= 1000) {
                headerName = "1km";
            } else {
                headerName = "Lebih dari 1km";//lebih dari 1 KM
            }

            ItemHeader<Store> itemHeader = new ItemHeader<>();
            itemHeader.setHeaderName(headerName);

            List<Store> stores2 = map.get(key);

            Collections.sort(stores2, new Comparator<Store>() {
                @Override
                public int compare(Store lhs, Store rhs) {

                    if (lhs.getId() != null && rhs.getId() != null) {
                        return rhs.getId().compareTo(lhs.getId());
                    }

                    return 0;
                }
            });

            itemHeader.setItems(stores2);
            result.add(itemHeader);
        }

        return result;
    }
}
