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

package horse.wtf.nzyme.events;

public class ShutdownEvent implements Event {

    @Override
    public TYPE type() {
        return TYPE.SHUTDOWN;
    }

    @Override
    public String name() {
        return "Shutdown";
    }

    @Override
    public String description() {
        return "Nzyme was shut down. This event will not be recorded during a system crash, but only for graceful shutdowns.";
    }

}
