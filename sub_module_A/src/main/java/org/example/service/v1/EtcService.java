package org.example.service.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtcService {

    private final Environment environment;

    public final List<String> getTimeZoneList() {
        return Arrays.asList(TimeZone.getAvailableIDs());
    }

    public final String getEnvironment(String key) {
        return environment.getProperty(key);
    }
}
