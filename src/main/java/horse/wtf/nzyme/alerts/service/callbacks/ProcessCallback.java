/*
 * This file is part of nzyme.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program. If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */

package horse.wtf.nzyme.alerts.service.callbacks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.auto.value.AutoValue;
import com.typesafe.config.Config;
import horse.wtf.nzyme.alerts.Alert;
import horse.wtf.nzyme.configuration.ConfigurationKeys;
import horse.wtf.nzyme.configuration.ConfigurationValidator;
import horse.wtf.nzyme.configuration.IncompleteConfigurationException;
import horse.wtf.nzyme.configuration.InvalidConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ProcessCallback implements AlertCallback {

    private static final Logger LOG = LogManager.getLogger(ProcessCallback.class);

    private final String command;
    private final int timeout_sec;
    private Object mutex = new Object();

    public ProcessCallback(Configuration config) {
        command = config.command();
        timeout_sec = config.timeout_sec();
    }

    @Override
    public void call(Alert alert) {
        String payload;
        try {
            payload = alert.toJSONString();
        } catch(JsonProcessingException e) {
            throw new RuntimeException("Could not transform alert to JSON.", e);
        }

        synchronized (mutex) {
            Process process;
            try {
                process = new ProcessBuilder(command).start();
            } catch (IOException e) {
                throw new RuntimeException("Failed to run sub-process.", e);
            }
              
            BufferedOutputStream process_stdin = new BufferedOutputStream(process.getOutputStream());

            try {
                process_stdin.write(payload.getBytes());
                process_stdin.write("\n".getBytes());
                process_stdin.close();
            } catch (Exception e) {
                throw new RuntimeException("Could not write alert to sub-process.", e);
            } finally {
                try {
                    if (!process.waitFor(timeout_sec, TimeUnit.SECONDS)) {
                        LOG.warn("Process timed out after {} seconds", timeout_sec);
                        process.destroy();
                    }
                } catch (Exception e) {
                    try {
                        process.destroyForcibly().waitFor();
                    } catch (InterruptedException e2) {
                        throw new RuntimeException("Failed to forcibly destroy sub-process.", e2);
                    }
                } finally {
                    try {
                        String line;

                        BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        while ((line = stdout.readLine()) != null) {
                            LOG.info("Process output: {}", line);
                        }
                        stdout.close();

                        BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        while ((line = stderr.readLine()) != null) {
                            LOG.info("Process error: {}", line);
                        }
                        stderr.close();
                    } catch (IOException e) {
                        // ignored
                    }
                }
            }
        }
    }

    private static final String WHERE = "alerting.callbacks.[process]";

    public static ProcessCallback.Configuration parseConfiguration(Config c) throws InvalidConfigurationException, IncompleteConfigurationException {
        ConfigurationValidator.expect(c, ConfigurationKeys.COMMAND, WHERE, String.class);
        ConfigurationValidator.expect(c, ConfigurationKeys.TIMEOUT_SEC, WHERE, Integer.class);

        String command;
        int timeout_sec;

        command = c.getString(ConfigurationKeys.COMMAND);
        timeout_sec = c.getInt(ConfigurationKeys.TIMEOUT_SEC);

        if (command == "") {
            throw new InvalidConfigurationException("Command cannot be empty.");
        }

        if (timeout_sec <= 0) {
            throw new InvalidConfigurationException("Timeout must be greater than zero.");
        }

        return Configuration.create(command, timeout_sec);
    }

    @AutoValue
    public static abstract class Configuration {

        public abstract String command();
        public abstract int timeout_sec();

      public static Configuration create(String command, int timeout_sec) {
            return builder()
                    .command(command)
                    .timeout_sec(timeout_sec)
                    .build();
        }

        public static Builder builder() {
            return new AutoValue_ProcessCallback_Configuration.Builder();
        }

        @AutoValue.Builder
        public abstract static class Builder {
            public abstract Builder command(String command);
            public abstract Builder timeout_sec(int timeout_sec);

            public abstract Configuration build();
        }

    }

}
