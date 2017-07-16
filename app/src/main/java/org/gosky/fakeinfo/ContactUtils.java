package org.gosky.fakeinfo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
  
public class ContactUtils {  
      
    /** 
     *  
     * 根据电话号码查询通讯录中的联系人姓名 
     * @param phonNum   联系人的号码 
     * @return  联系人的姓名 
     */  
    public static String findNameFromContact(String phonNum,Map<String, String> allContacts){
        if(!TextUtils.isEmpty(phonNum) && allContacts != null && allContacts.size() > 0 ){  
            String phone = handlePhoneNum(phonNum);//将号码处理后再进行查询  
            if(allContacts.containsKey(phone)){  
                return allContacts.get(phone);  
            }  
        }  
        return null;  
    }   
      
    /** 
     *  
     * 获取通讯录中的所有联系人存储在Map中 
     * 注：Map中号码唯一作为key，号码对应的联系人作为value 
     * 对同一个号码存储多个联系人姓名的做了处理，这里只存一个姓名。即一个号码只对应一个姓名 
     * @param context 
     * @return 
     */  
    public static Map<String,String> getAllContacts(Context context){  
        Map<String,String> contactsMap = new HashMap<String,String>();  
        //生成ContentResolver对象  
        ContentResolver contentResolver = context.getContentResolver();
        // 获得所有的联系人  
        /*Cursor cursor = contentResolver.query( 
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null); 
         */  
        //这段代码和上面代码是等价的，使用两种方式获得联系人的Uri  
        Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/contacts"),null,null,null,null);  
        // 循环遍历  
        if (cursor.moveToFirst()) {  
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);  
            int displayNameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);  
            do {  
                // 获得联系人的ID  
                String contactId = cursor.getString(idColumn);  
                // 获得联系人姓名  
                String displayName = cursor.getString(displayNameColumn);  
                //显示获得的联系人信息  
                System.out.println("联系人姓名：" + displayName);  
                // 查看联系人有多少个号码，如果没有号码，返回0  
                int phoneCount = cursor.getInt(cursor  
                                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));  
                if (phoneCount > 0) {  
                    // 获得联系人的电话号码列表  
                    Cursor phoneCursor = context.getContentResolver().query(  
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,  
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID  
                                    + "=" + contactId, null, null);  
                    if(phoneCursor.moveToFirst()){  
                        do{  
                            //遍历所有的联系人下面所有的电话号码  
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));  
                            //显示获得的号码  
                             System.out.println("联系人电话："+phoneNumber);  
                             if(!contactsMap.containsKey(phoneNumber)){  
                                 contactsMap.put(handlePhoneNum(phoneNumber), displayName);  
                             }  
                        }while(phoneCursor.moveToNext());  
                    }  
                }  
            } while (cursor.moveToNext());  
        }  
        return contactsMap;  
    }  
      
    /** 
     *  
     * 处理号码的方法 
     * 规则： 
     * 1、去除号码中所有的非数字 
     * 2、如果号码为13位（即手机号）就去掉86 
     * @param phoneNum  
     * @return 
     */  
    private static String handlePhoneNum(String phoneNum){  
        if(!TextUtils.isEmpty(phoneNum)){  
            phoneNum = phoneNum.replaceAll("\\D", "");  
        }  
        if(phoneNum.length() == 13 && phoneNum.startsWith("86")){  
            return phoneNum.substring(2);  
        }  
        return phoneNum;  
    }  
}  