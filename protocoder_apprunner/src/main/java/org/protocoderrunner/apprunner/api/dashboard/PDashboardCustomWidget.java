/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices 
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
* 
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoderrunner.apprunner.api.dashboard;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.network.CustomWebsocketServer;
import org.protocoderrunner.network.CustomWebsocketServer.WebSocketListener;
import org.protocoderrunner.utils.StrUtils;

import java.net.UnknownHostException;

public class PDashboardCustomWidget extends PInterface {

    private static final String TAG = "PDashboardCustomWidget";
    String id;

    public PDashboardCustomWidget(Context a) {
        super(a);
    }

    // --------- JDashboard add ---------//
    public interface jDashboardAddCB {
        void event(JSONObject obj);
    }

    public void add(String url, int x, int y, int w, int h, final jDashboardAddCB callbackfn) throws JSONException,
            UnknownHostException {
        this.id = StrUtils.generateRandomString();

        JSONObject values = new JSONObject()
                .put("id", id)
                .put("url", url)
                .put("type", "custom")
                .put("x", x)
                .put("y", y)
                .put("w", w)
                .put("h", h);

        JSONObject msg = new JSONObject()
                .put("type", "widget")
                .put("action", "add")
                .put("values", values);

        CustomWebsocketServer.getInstance(getContext()).send(msg);
        CustomWebsocketServer.getInstance(getContext()).addListener(id, new WebSocketListener() {

            @Override
            public void onUpdated(final JSONObject jsonObject) {
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        callbackfn.event(jsonObject);
                    }
                });
            }
        });

    }

    public void send(JSONObject obj) throws JSONException, UnknownHostException {

        JSONObject values = new JSONObject()
                .put("id", id)
                .put("type", "custom")
                .put("val", obj);

        JSONObject msg = new JSONObject()
                .put("type", "widget")
                .put("action", "send")
                .put("values", values);

        CustomWebsocketServer.getInstance(getContext()).send(msg);
    }
}
