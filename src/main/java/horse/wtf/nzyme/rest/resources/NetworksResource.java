/*
 *  This file is part of nzyme.
 *
 *  nzyme is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  nzyme is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with nzyme.  If not, see <http://www.gnu.org/licenses/>.
 */

package horse.wtf.nzyme.rest.resources;

import horse.wtf.nzyme.Nzyme;
import horse.wtf.nzyme.dot11.networks.BSSID;
import horse.wtf.nzyme.dot11.networks.Channel;
import horse.wtf.nzyme.dot11.networks.SSID;
import horse.wtf.nzyme.rest.responses.networks.BSSIDsResponse;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/networks")
@Produces(MediaType.APPLICATION_JSON)
public class NetworksResource {

    @Inject
    private Nzyme nzyme;

    @GET
    @Path("/bssids")
    public Response bssids() {
        return Response.ok(BSSIDsResponse.create(
                nzyme.getNetworks().getBSSIDs().size(),
                nzyme.getNetworks().getBSSIDs()
        )).build();
    }

    @POST
    @Path(("/fingerprints/reset"))
    public Response resetFingerprints() {
        for (BSSID bssid : nzyme.getNetworks().getBSSIDs().values()) {
            for (SSID ssid : bssid.ssids().values()) {
                for (Channel channel : ssid.channels().values()) {
                    channel.fingerprints().clear();
                }
            }
        }

        return Response.ok().build();
    }

}
