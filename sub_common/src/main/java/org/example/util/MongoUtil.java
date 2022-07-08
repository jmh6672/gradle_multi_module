package org.example.util;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Optional;


@Slf4j
public class MongoUtil {

    public static String getIdFromObject (Object resultObject) {
        String _id = null;
        try {
            Field field = resultObject.getClass().getDeclaredField("_id");
            field.setAccessible(true);
            _id = ((ObjectId) field.get(resultObject)).toHexString();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("can't find '_id' filed from {}", resultObject.getClass().getName());
        }

        return _id;
    }

    /**
     * entity 의 요소에 대한 update object 를 자동으로 생성하기위함
     * @param updateObject MongoDB 용 entity 로만 사용 (Map 사용 X)
     * @return Update
     */
    public static Update getUpdate(Object updateObject){
        return getUpdate(updateObject,null);
    }

    /**
     * Update 시 MongoDB 대응 entity 에 값이 존재하는 항목만 추출 적용
     * @param updateObject : MongoDB 용 entity 로만 사용 (Map 사용 X)
     * @param prefix : 2depth (nested object) 일 경우 1depth (collection) 명칭 기입
     *                (ex. "flow.serviceOption" 일 경우 "flow." 입력)
     * @return Update
     */
    public static Update getUpdate(Object updateObject, String prefix) {
        if(prefix == null) {
            prefix = "";
        }

        Field[] fields = updateObject.getClass().getDeclaredFields();

        Update update = new Update();

        if(fields.length > 0){
            for (Field field : fields) {
                field.setAccessible(true);
                String key = prefix + field.getName();
                Object value = null;
                try {
                    value = field.get(updateObject);
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage());
                    log.warn("Failed to get {} field value in {} Class",key,updateObject.getClass().getName());
                }

                if(key.equalsIgnoreCase("updateUserId")){
                    update.set("updateUserId", value);
                }else if(key.equalsIgnoreCase("updateDate")){
                    update.set("updateDate", Instant.now());
                }else if(key.equalsIgnoreCase("createUserId")) {
                    update.setOnInsert("createUserId", value);
                }else if(key.equalsIgnoreCase("createDate")) {
                    update.setOnInsert("createDate", Instant.now());
                }else{
                    Object finalValue = value;
                    Optional.ofNullable(value).ifPresent(none -> update.set(key, finalValue));
                }
            }
        }

        return update;
    }

    public static Update getUpdateExceptNested(Object updateObject) {
        Field[] fields = updateObject.getClass().getDeclaredFields();

        Update update = new Update();

        if(fields.length > 0){
            for (Field field : fields) {
                field.setAccessible(true);
                String key = field.getName();
                Object value = null;
                try {
                    value = field.get(updateObject);
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage());
                    log.warn("Failed to get {} field value in {} Class",key,updateObject.getClass().getName());
                }

                if(key.equalsIgnoreCase("updateUserId")) {
                    update.set("updateUserId", value);
                } else if(key.equalsIgnoreCase("updateDate")) {
                    update.set("updateDate", Instant.now());
//                } else if(key.equalsIgnoreCase("createUserId")) {
//                    update.setOnInsert("createUserId", value);
//                } else if(key.equalsIgnoreCase("createDate")) {
//                    update.setOnInsert("createDate", Instant.now());
                } else if(value != null) {
                    Object finalValue = value;
                    String typeName = finalValue.getClass().getName().toLowerCase();

                    if( !(
                            typeName.contains("list")  // java.util.arraylist
                        || (typeName.contains("entity") && typeName.contains("&"))  // ipron.cloud.web.entity.user$schedule
                    ) )  {
                       Optional.ofNullable(value).ifPresent(none -> update.set(key, finalValue));
                    }

                }
            }
        }

        return update;
    }
}
