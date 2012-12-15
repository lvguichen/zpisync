package zpisync.desktop;

import org.teleal.cling.UpnpService;
import org.teleal.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionArgumentValue;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.Service;

import zpisync.shared.services.UpnpZpiSync;

public class UpnpZpiSyncClient {

	UpnpService upnpService;
	Service service;
	
	public UpnpZpiSyncClient(UpnpService upnpService, Device device) {
		this.upnpService = upnpService; 
		this.service = findService(device, UpnpZpiSync.class);
	}
	
	private static Service<?, ?> findService(Device<?, ?, ?> device, Class<?> serviceType) {
		LocalService service = new AnnotationLocalServiceBinder().read(serviceType);
		return device.findService(service.getServiceType());
	}
	
	public String getEndpointUrl() {
		Action getEndpointUrlAction = service.getAction("GetEndpointUrl");
		ActionInvocation getEndpointUrlInvocation = new ActionInvocation(getEndpointUrlAction);
		new ActionCallback.Default(getEndpointUrlInvocation, upnpService.getControlPoint()).run();
		ActionArgumentValue output = getEndpointUrlInvocation.getOutput("EndpointUrl");
		if (output == null) {
			throw new Error("No endpoint output");
		}
		String endpoint = (String) output.getValue();
		return endpoint;
	}
}
