package org.example.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Configuration
@Slf4j
public class JsonConfig {
    /**
     * json 변환시 ObjectId class 를 String 으로 변환
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder
//                .modulesToInstall(new JavaTimeModule()
//                        .addDeserializer(Instant.class, new InstantFormatDeserializer())
//                )
                .deserializerByType(Instant.class, new InstantFormatDeserializer())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    /**
     * json String 타입을 object로 변환시 Instant deserializer 정의
    * */
    public class InstantFormatDeserializer extends JsonDeserializer<Instant> {
        private InstantDeserializer<Instant> deserializer = InstantDeserializer.INSTANT;

        private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        @Override
        public Instant deserialize(JsonParser p, DeserializationContext context) throws IOException {
            // String을 instant로 변환시 string 길이가 8이면 yyyyMMdd 패턴으로 파싱
            String str = p.getValueAsString();
            if(str != null && str.length() == 8){
                LocalDate date = LocalDate.parse(p.getText(),formatter);

                return date.atStartOfDay(context.getTimeZone().toZoneId()).toInstant();
            }

            return deserializer.deserialize(p,context);
        }
    }
}
