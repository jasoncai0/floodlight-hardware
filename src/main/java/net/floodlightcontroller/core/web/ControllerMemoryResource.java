/**
*    Copyright 2011, Big Switch Networks, Inc.
*    Originally created by David Erickson, Stanford University
*
*    Licensed under the Apache License, Version 2.0 (the "License"); you may
*    not use this file except in compliance with the License. You may obtain
*    a copy of the License at
*
*         http://www.apache.org/licenses/LICENSE-2.0
*
*    Unless required by applicable law or agreed to in writing, software
*    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
*    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
*    License for the specific language governing permissions and limitations
*    under the License.
**/

package net.floodlightcontroller.core.web;

import java.util.HashMap;
import java.util.Map;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * Retrieve floodlight memory state
 * @author readams
 */
public class ControllerMemoryResource extends ServerResource {
    @Get("json")
    public Map<String, Object> retrieve() {
        HashMap<String, Object> model = new HashMap<String, Object>();
        Runtime runtime = Runtime.getRuntime();
        model.put("total", new Long(runtime.totalMemory()));
        model.put("free", new Long(runtime.freeMemory()));
        return model;
    }
}
