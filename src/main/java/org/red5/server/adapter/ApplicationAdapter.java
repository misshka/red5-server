/*
 * RED5 Open Source Flash Server - https://github.com/Red5/
 * 
 * Copyright 2006-2016 by respective authors (see below). All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.red5.server.adapter;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;

/**
 * Base class for applications, takes care that callbacks are executed single-threaded. If you want to have maximum performance, use
 * {@link MultiThreadedApplicationAdapter} instead.
 * 
 * Using this class may lead to problems if accepting a client in the <code>Connect</code> or <code>Join</code> methods takes too
 * long, so using the multi-threaded version is preferred.
 * 
 * @author The Red5 Project
 * @author Joachim Bauch (jojo@struktur.de)
 * @author Paul Gregoire (mondain@gmail.com)
 */
public class ApplicationAdapter extends MultiThreadedApplicationAdapter {

    private ReentrantLock lock = new ReentrantLock(true);

    /** {@inheritDoc} */
    @Override
    public boolean start(IScope scope) {
        try {
            if (lock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    return super.start(scope);
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            log.error("Failed to lock", e);
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void stop(IScope scope) {

        try {
            if (lock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    super.stop(scope);
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            log.error("Failed to lock", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean connect(IConnection conn, IScope scope, Object[] params) {
        try {
            if (lock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    return super.connect(conn, scope, params);
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            log.error("Failed to lock", e);
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void disconnect(IConnection conn, IScope scope) {
        lock.lock();
        try {
            super.disconnect(conn, scope);
        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean join(IClient client, IScope scope) {
        try {
            if (lock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    return super.join(client, scope);
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            log.error("Failed to lock", e);
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void leave(IClient client, IScope scope) {
        lock.lock();
        try {
            super.leave(client, scope);
        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }

}
