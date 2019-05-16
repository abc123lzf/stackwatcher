package com.lzf.stackwatcher.alert.configuration;

import com.lzf.stackwatcher.common.AbstractConfig;
import com.lzf.stackwatcher.common.ConfigInitializationException;
import com.lzf.stackwatcher.common.ConfigManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class AlertMessageConfig extends AbstractConfig {
    public static final String NAME = "config.alert-message";
    private static final String PATH = "classpath://alert-message-pattern";

    private String pattern;

    public AlertMessageConfig(ConfigManager configManager) {
        super(configManager, NAME);
    }

    @Override
    protected void initInternal() throws ConfigInitializationException {
        try(InputStream is = configManager.loadResource(PATH)) {
            InputStreamReader reader = new InputStreamReader(is, Charset.forName("UTF-8"));
            CharBuffer buf = CharBuffer.wrap(new StringBuilder());
            int r = reader.read(buf);
            char[] c = new char[r];
            buf.get(c, 0, r);
            pattern = new String(c);
        } catch (Exception e) {
            throw new ConfigInitializationException(e);
        }
    }

    public String getPattern() {
        return pattern;
    }
}
