package com.toyota.tcapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.smartdevicelink.transport.TCPTransportConfig;
import com.toyota.brett.tcapp.R;
import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.RPCResponse;
import com.smartdevicelink.proxy.SdlProxyALM;
import com.smartdevicelink.proxy.SystemCapabilityManager;
import com.smartdevicelink.proxy.interfaces.IProxyListenerALM;
import com.smartdevicelink.proxy.interfaces.OnSystemCapabilityListener;
import com.smartdevicelink.proxy.rpc.AddCommand;
import com.smartdevicelink.proxy.rpc.AddSubMenu;
import com.smartdevicelink.proxy.rpc.ButtonPressResponse;
import com.smartdevicelink.proxy.rpc.DisplayCapabilities;
import com.smartdevicelink.proxy.rpc.GetInteriorVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.GetSystemCapabilityResponse;
import com.smartdevicelink.proxy.rpc.GetVehicleData;
import com.smartdevicelink.proxy.rpc.GetWayPointsResponse;
import com.smartdevicelink.proxy.rpc.Image;
import com.smartdevicelink.proxy.rpc.MenuParams;
import com.smartdevicelink.proxy.rpc.OnHMIStatus;
import com.smartdevicelink.proxy.rpc.OnInteriorVehicleData;
import com.smartdevicelink.proxy.rpc.OnWayPointChange;
import com.smartdevicelink.proxy.rpc.SendHapticDataResponse;
import com.smartdevicelink.proxy.rpc.SetDisplayLayout;
import com.smartdevicelink.proxy.rpc.SetInteriorVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.Show;
import com.smartdevicelink.proxy.rpc.SubscribeButton;
import com.smartdevicelink.proxy.rpc.SubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.enums.ButtonName;
import com.smartdevicelink.proxy.rpc.enums.ImageType;
import com.smartdevicelink.proxy.rpc.enums.Result;
import com.smartdevicelink.proxy.rpc.enums.SdlDisconnectedReason;
import com.smartdevicelink.proxy.rpc.enums.SpeechCapabilities;
import com.smartdevicelink.proxy.rpc.enums.SystemCapabilityType;
import com.smartdevicelink.proxy.rpc.enums.TextAlignment;
import com.smartdevicelink.proxy.rpc.listeners.OnMultipleRequestListener;
import com.smartdevicelink.proxy.rpc.listeners.OnRPCResponseListener;
import com.smartdevicelink.transport.BaseTransportConfig;
import com.smartdevicelink.transport.MultiplexTransportConfig;
import com.smartdevicelink.transport.TransportConstants;
import com.smartdevicelink.proxy.callbacks.OnServiceEnded;
import com.smartdevicelink.proxy.callbacks.OnServiceNACKed;
import com.smartdevicelink.proxy.rpc.AddCommandResponse;
import com.smartdevicelink.proxy.rpc.AddSubMenuResponse;
import com.smartdevicelink.proxy.rpc.AlertManeuverResponse;
import com.smartdevicelink.proxy.rpc.AlertResponse;
import com.smartdevicelink.proxy.rpc.ChangeRegistrationResponse;
import com.smartdevicelink.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteCommandResponse;
import com.smartdevicelink.proxy.rpc.DeleteFileResponse;
import com.smartdevicelink.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteSubMenuResponse;
import com.smartdevicelink.proxy.rpc.DiagnosticMessageResponse;
import com.smartdevicelink.proxy.rpc.DialNumberResponse;
import com.smartdevicelink.proxy.rpc.EndAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.GenericResponse;
import com.smartdevicelink.proxy.rpc.GetDTCsResponse;
import com.smartdevicelink.proxy.rpc.GetVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.ListFilesResponse;
import com.smartdevicelink.proxy.rpc.OnAudioPassThru;
import com.smartdevicelink.proxy.rpc.OnButtonEvent;
import com.smartdevicelink.proxy.rpc.OnButtonPress;
import com.smartdevicelink.proxy.rpc.OnCommand;
import com.smartdevicelink.proxy.rpc.OnDriverDistraction;
import com.smartdevicelink.proxy.rpc.OnHashChange;
import com.smartdevicelink.proxy.rpc.OnKeyboardInput;
import com.smartdevicelink.proxy.rpc.OnLanguageChange;
import com.smartdevicelink.proxy.rpc.OnLockScreenStatus;
import com.smartdevicelink.proxy.rpc.OnPermissionsChange;
import com.smartdevicelink.proxy.rpc.OnStreamRPC;
import com.smartdevicelink.proxy.rpc.OnSystemRequest;
import com.smartdevicelink.proxy.rpc.OnTBTClientState;
import com.smartdevicelink.proxy.rpc.OnTouchEvent;
import com.smartdevicelink.proxy.rpc.OnVehicleData;
import com.smartdevicelink.proxy.rpc.PerformAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.PerformInteractionResponse;
import com.smartdevicelink.proxy.rpc.PutFile;
import com.smartdevicelink.proxy.rpc.PutFileResponse;
import com.smartdevicelink.proxy.rpc.ReadDIDResponse;
import com.smartdevicelink.proxy.rpc.ResetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.ScrollableMessageResponse;
import com.smartdevicelink.proxy.rpc.SendLocationResponse;
import com.smartdevicelink.proxy.rpc.SetAppIconResponse;
import com.smartdevicelink.proxy.rpc.SetDisplayLayoutResponse;
import com.smartdevicelink.proxy.rpc.SetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.SetMediaClockTimerResponse;
import com.smartdevicelink.proxy.rpc.ShowConstantTbtResponse;
import com.smartdevicelink.proxy.rpc.ShowResponse;
import com.smartdevicelink.proxy.rpc.SliderResponse;
import com.smartdevicelink.proxy.rpc.SpeakResponse;
import com.smartdevicelink.proxy.rpc.StreamRPCResponse;
import com.smartdevicelink.proxy.rpc.SubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.SubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.SystemRequestResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.UpdateTurnListResponse;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.enums.HMILevel;
import com.smartdevicelink.proxy.rpc.enums.LockScreenStatus;
import com.smartdevicelink.util.CorrelationIdGenerator;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * This is where the magic happens (in order):
 *
 * 1. SDL Proxy Object is created in OnStartCommand
 * 2. Once onOnHMIStatus returns HMI_FULL, we set the layout in sendDisplayLayout.
 *    In HMI_NONE, we can check to see if graphics supported, if so we can set the app icon here
 * 3. We will receive back a "SUCCESS" response from CORE in the onSetDisplayLayoutResponse method
 * 4. Once successful response is had, call createTextFields to send RPC to set text fields. We will
 *    then set the buttons, which need to be called individually. Then finally we set our image after checking
 *    if its allowed, upload it to core, and then set it.
 *
 **/


public class SdlService extends Service implements IProxyListenerALM {

    //APP
    private static final String APP_NAME = "SDLTESTAPP";
    private static final String APP_ID = "534634765";
    private static final String APP_ICON = "sdlicon.jpg";
    private static final Integer APP_ICON_RESOURCE = R.drawable.sdlicon;

    //CORE
    private static final String CORE_IP = "192.168.1.167";
    private static final int CORE_PORT = 12345;
    private static final String TAG = "SDL Service";

	private static final String TEST_COMMAND_NAME 		= "Test Command";
	private static final int TEST_COMMAND_ID 			= 2;

    // Interface style. Generic HMI currently supports
    // MEDIA, NON-MEDIA, LARGE-GRAPHIC-ONLY
    private static final String INTERFACE = "LARGE_GRAPHIC";

	private static final int FOREGROUND_SERVICE_ID = 849;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private SdlProxyALM proxy = null;
	BaseTransportConfig transport = null;

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			enterForeground();
		}
	}

	@SuppressLint("NewApi")
	public void enterForeground() {
		Notification notification = new Notification.Builder(this)
				.setContentTitle("SmartDeviceLink")
				.setContentText(getString(R.string.app_name))
				.setSmallIcon(R.drawable.ic_sdl)
				.setTicker("SmartDeviceLink")
				.setPriority(Notification.PRIORITY_DEFAULT)
				.build();
		startForeground(FOREGROUND_SERVICE_ID, notification);
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			startForeground(849 ,(Notification) intent.getParcelableExtra("notification") );
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean forceConnect = intent !=null && intent.getBooleanExtra(TransportConstants.FORCE_TRANSPORT_CONNECTED, false);
        if (proxy == null) {
            try {
                //Create a new proxy using Bluetooth transport
                //The listener, app name,
                //whether or not it is a media app and the applicationId are supplied.
                //proxy = new SdlProxyALM(this, APP_NAME, false, APP_ID,new MultiplexTransportConfig(getBaseContext(), APP_ID));
//				transport = new BTTransportConfig();
				transport = new MultiplexTransportConfig(getBaseContext(), APP_ID);
//				transport = new USBTransportConfig(getBaseContext(), (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY));
				//proxy = new SdlProxyALM(this, APP_NAME, true, APP_ID, transport);
                // USE TCP FOR EMULATOR (no BlueTooth)
                proxy = new SdlProxyALM(this,APP_NAME, false, APP_ID ,new TCPTransportConfig(CORE_PORT, CORE_IP, false));

            } catch (SdlException e) {
                //There was an error creating the proxy
                if (proxy == null) {
                    //Stop the SdlService
                    stopSelf();
                }
            }
        }else if(forceConnect){
            proxy.forceOnConnected();
        }

        //use START_STICKY because we want the SDLService to be explicitly started and stopped as needed.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //Dispose of the proxy
        if (proxy != null) {
            try {
                proxy.dispose();
            } catch (SdlException e) {
                e.printStackTrace();
            } finally {
                proxy = null;
            }
        }

        sendBroadcast(new Intent("CLOSE_LOCK_SCREEN"));

        super.onDestroy();
    }

    @Override
    public void onProxyClosed(String info, Exception e, SdlDisconnectedReason reason) {
        //Stop the service
        stopSelf();

        //learn reason
        Log.e("PROXY CLOSED REASON", reason.toString());

    }

    @Override
    public void onOnHMIStatus(OnHMIStatus notification) {
		try {
			Log.i(TAG, "HMI NOTIFICATION: "+notification.serializeJSON().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		switch(notification.getHmiLevel()) {
            case HMI_FULL:
                //send welcome message, addcommands, subscribe to buttons ect

                // Set display layout
                sendDisplayLayout();
				getVIN();
                break;
            case HMI_LIMITED:
                break;
            case HMI_BACKGROUND:
                break;
            case HMI_NONE:

                // in here we can set app icon if graphics supported
                boolean supported = graphicsSupported();
                // if supported, we can upload our image
                if (supported) {
                    //we must send image to core before using it.
                    putAndSetAppIcon();
                }
                getCapabilities();

                break;
            default:
                return;
        }
    }

	private void sendCommands(){
		AddCommand command = new AddCommand();
		MenuParams params = new MenuParams();
		params.setMenuName(TEST_COMMAND_NAME);
		command.setCmdID(TEST_COMMAND_ID);
		command.setMenuParams(params);
		command.setVrCommands(Collections.singletonList(TEST_COMMAND_NAME));
		try {
			proxy.sendRPCRequest(command);
		} catch (SdlException e) {
			e.printStackTrace();
		}
	}

    public void sendDisplayLayout(){
        SetDisplayLayout setDisplayLayoutRequest = new SetDisplayLayout();
        setDisplayLayoutRequest.setDisplayLayout(INTERFACE);
        setDisplayLayoutRequest.setCorrelationID(CorrelationIdGenerator.generateId());
        try{
            proxy.sendRPCRequest(setDisplayLayoutRequest);
        }catch (SdlException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse response) {
        Log.i(TAG, "SetDisplayLayout response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

        // Once Layout Is Set, Send Over Text Fields
        createTextFields();

        // create buttons
        createButtons();

        // put and set image
        String picName = "cartman.jpg";
        putImage(picName, FileType.GRAPHIC_JPEG, false, R.drawable.cartman);

        //create menu for our app
        sendCommands();

        // capabilities
		getCapabilities();

        // try multiple sending
		//sendMultipleRPCs();

		addSubMenu();
    }

    public void createTextFields(){
        Show show = new Show();

        // Fields will change depending on layout used
        show.setMainField1("Season 1 Theme Song");
        show.setMainField2("South Park Album");
        show.setMainField3("South Park");
        show.setCorrelationID(CorrelationIdGenerator.generateId());

        try {
            proxy.sendRPCRequest(show);
        } catch (SdlException e) {
            e.printStackTrace();
        }
    }

    public void addSubMenu() {
		int unique_id = 313;

		AddSubMenu addSubMenu = new AddSubMenu();
		addSubMenu.setPosition(0);
		addSubMenu.setMenuID(unique_id);
		addSubMenu.setMenuName("SubMenu");
		addSubMenu.setOnRPCResponseListener(new OnRPCResponseListener() {
			@Override
			public void onResponse(int correlationId, RPCResponse response) {
				if(((AddSubMenuResponse) response).getSuccess()){
					// The submenu was created successfully, start adding the submenu items
					// Use unique_id
				}else{
					Log.i("SdlService", "AddSubMenu request rejected.");
				}
			}
		});
		try {
			proxy.sendRPCRequest(addSubMenu);
		} catch (SdlException e) {
			e.printStackTrace();
		}
	}


    public void createButtons(){

        Log.i(TAG,"SdlService "+"CREATE BUTTONS CALLED");

        SubscribeButton subscribeButtonRequestLeft = new SubscribeButton();
        subscribeButtonRequestLeft.setButtonName(ButtonName.SEEKLEFT);
        subscribeButtonRequestLeft.setCorrelationID(CorrelationIdGenerator.generateId());

        SubscribeButton subscribeButtonRequestOk = new SubscribeButton();
        subscribeButtonRequestOk.setButtonName(ButtonName.OK);
        subscribeButtonRequestOk.setCorrelationID(CorrelationIdGenerator.generateId());

        SubscribeButton subscribeButtonRequestRight = new SubscribeButton();
        subscribeButtonRequestRight.setButtonName(ButtonName.SEEKRIGHT);
        subscribeButtonRequestRight.setCorrelationID(CorrelationIdGenerator.generateId());

        try {
            proxy.sendRPCRequest(subscribeButtonRequestLeft);
            proxy.sendRPCRequest(subscribeButtonRequestOk);
            proxy.sendRPCRequest(subscribeButtonRequestRight);
        } catch (SdlException e) {
            e.printStackTrace();
        }

    }

    boolean graphicsSupported(){
        Boolean graphicsSupported = false;
        try {
            DisplayCapabilities displayCapabilities = proxy.getDisplayCapabilities();
            graphicsSupported = displayCapabilities.getGraphicSupported();
            return graphicsSupported;
        } catch (SdlException e) {
            e.printStackTrace();
        }
        Log.i(TAG,"SdlService "+"Graphics Supported: "+graphicsSupported);
        return graphicsSupported;
    }

    public void sendMultipleRPCs() {
    	List<RPCRequest> rpcs = new ArrayList<>();

    	// rpc 1
		Show show = new Show();
		show.setMainField1("hey friends");
		show.setMainField2("");
		show.setMainField3("");
		show.setMainField4("");
		rpcs.add(show);

		// rpc 2
		Show show2 = new Show();
		show2.setMainField1("");
		show2.setMainField2("Its Weds My Dudes");
		show2.setMainField3("");
		show2.setMainField4("");
		rpcs.add(show2);

		try {
			proxy.sendRequests(rpcs, new OnMultipleRequestListener() {
				@Override
				public void onUpdate(int remainingRequests) {
					Log.i(TAG, "MULTIPLE ON UPDATE: " + String.valueOf(remainingRequests));
				}

				@Override
				public void onFinished() {
					Log.i(TAG, "MULTIPLE ON FINISHED");
				}

				@Override
				public void onResponse(int correlationId, RPCResponse response) {
					Log.i(TAG, "MULTIPLE ON RESPONSE: " + String.valueOf(correlationId));
				}

				@Override
				public void onError(int correlationId, Result resultCode, String info) {
					Log.e(TAG, "MULTIPLE ON ERROR: " + String.valueOf(correlationId));
				}
			});
		} catch (SdlException e) {
			e.printStackTrace();
		}
	}

    public void getCapabilities() {
		Log.i(TAG,"GET Capability CALLED");

		proxy.getCapability(SystemCapabilityType.SPEECH);

		proxy.getCapability(SystemCapabilityType.SPEECH, new OnSystemCapabilityListener(){

			@Override
			public void onCapabilityRetrieved(Object capability){
				List<SpeechCapabilities> speechCapabilityList = SystemCapabilityManager.convertToList(capability, SpeechCapabilities.class);
				Log.i(TAG, "RAIR "+ speechCapabilityList.toString());
			}

			@Override
			public void onError(String info){
				Log.i(TAG, "Capability could not be retrieved: "+ info);
			}
		});
	}

	public void getVIN() {
		GetVehicleData vdRequest = new GetVehicleData();
		vdRequest.setVin(true);
		vdRequest.setOnRPCResponseListener(new OnRPCResponseListener() {
			@Override
			public void onResponse(int correlationId, RPCResponse response) {
				if(response.getSuccess()){
					String VIN = ((GetVehicleDataResponse) response).getVin();
					Log.i("SdlService", "VIN: " + VIN);
				}else{
					Log.i("SdlService", "GetVehicleData was rejected.");
				}
			}
		});
		try {
			proxy.sendRPCRequest(vdRequest);
		} catch (SdlException e) {
			e.printStackTrace();
		}
	}


    public void putAndSetAppIcon(){
		byte[] file = contentsOfResource(APP_ICON_RESOURCE);
        PutFile putFileRequest = new PutFile();
        putFileRequest.setSdlFileName(APP_ICON);
        putFileRequest.setFileType(FileType.GRAPHIC_JPEG);
        putFileRequest.setPersistentFile(true);
        putFileRequest.setFileData(file); // can create file_data using helper method below
		putFileRequest.setCRC(file);
        putFileRequest.setCorrelationID(CorrelationIdGenerator.generateId());
        putFileRequest.setOnRPCResponseListener(new OnRPCResponseListener() {

            @Override
            public void onResponse(int correlationId, RPCResponse response) {
                setListenerType(UPDATE_LISTENER_TYPE_PUT_FILE); // necessary for PutFile requests

                if(response.getSuccess()){
                    try {
                        proxy.setappicon(APP_ICON, CorrelationIdGenerator.generateId());
                    } catch (SdlException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.i(TAG,"SdlService "+"Unsuccessful app icon upload.");
                }
            }
        });
        try {
            proxy.sendRPCRequest(putFileRequest);
        } catch (SdlException e) {
            e.printStackTrace();
        }
    }

    public void putImage(final String fileName, final FileType fileType, final boolean persistent, final Integer id){
		final byte[] data = contentsOfResource(id);
        PutFile putFileRequest = new PutFile();
        putFileRequest.setSdlFileName(fileName);
        putFileRequest.setFileType(FileType.GRAPHIC_JPEG);
        putFileRequest.setPersistentFile(true);
        putFileRequest.setCRC(data);
        putFileRequest.setFileData(data); // can create file_data using helper method below
        putFileRequest.setCorrelationID(CorrelationIdGenerator.generateId());
        putFileRequest.setOnRPCResponseListener(new OnRPCResponseListener() {

            @Override
            public void onResponse(int correlationId, RPCResponse response) {
                setListenerType(UPDATE_LISTENER_TYPE_PUT_FILE); // necessary for PutFile requests

                if(response.getSuccess()){
                    try {
                        proxy.putfile(fileName, fileType, persistent, data, id);
                        setImage(fileName);
                        Log.i(TAG,"SdlService "+"IMAGE PUT SUCCESSFULLY.");
                    } catch (SdlException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.i(TAG,"SdlService "+"Unsuccessful app icon upload.");
                }
            }
        });
        try {
            proxy.sendRPCRequest(putFileRequest);
        } catch (SdlException e) {
            e.printStackTrace();
        }
    }

    public void setImage(String fileName){
        Image image = new Image();
        image.setImageType(ImageType.DYNAMIC);
        image.setValue(fileName); // a previously uploaded filename using PutFile RPC

        Show show = new Show();
        show.setGraphic(image);
        show.setCorrelationID(CorrelationIdGenerator.generateId());
        show.setOnRPCResponseListener(new OnRPCResponseListener() {
            @Override
            public void onResponse(int correlationId, RPCResponse response) {
                if (response.getSuccess()) {
                    Log.i(TAG,"SdlService "+"IMAGE SUCCESSFULLY SHOWED.");
                } else {
                    Log.i(TAG,"SdlService "+"IMAGE REJECTED");
                }
            }
        });
        try {
            proxy.sendRPCRequest(show);
        } catch (SdlException e) {
            e.printStackTrace();
        }
    }


	@Override
	public void onOnCommand(OnCommand notification){
		Integer id = notification.getCmdID();
		if(id != null){
			switch(id){
				case TEST_COMMAND_ID:
					if (proxy != null) {
						showTest();
					}
					break;
			}
		}
	}

	/**
	 * Will show a sample test message on screen as well as speak a sample test message
	 */
	private void showTest(){
		try {
			proxy.show(TEST_COMMAND_NAME, "Command has been selected", TextAlignment.CENTERED, CorrelationIdGenerator.generateId());
			proxy.speak(TEST_COMMAND_NAME, CorrelationIdGenerator.generateId());
		} catch (SdlException e) {
			e.printStackTrace();
		}
	}

    @Override
    public void onShowResponse(ShowResponse response) {
        Log.i(TAG, "Show response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo() + " Corr ID: " + response.getCorrelationID());


        if (response.getSuccess()) {
            Log.i(TAG,"SdlService "+"Successfully showed.");
        } else {
            Log.i(TAG,"SdlService "+"Show request was rejected. "+response.getInfo());
        }
    }

    /**
     * Rest of the SDL callbacks from the head unit
     */

    @Override
    public void onOnPermissionsChange(OnPermissionsChange notification) {
        Log.i(TAG, "Permision changed: " + notification);
		/* Uncomment to subscribe to vehicle data
		List<PermissionItem> permissions = notification.getPermissionItem();
		for(PermissionItem permission:permissions){
			if(permission.getRpcName().equalsIgnoreCase(FunctionID.SUBSCRIBE_VEHICLE_DATA.name())){
				if(permission.getHMIPermissions().getAllowed()!=null && permission.getHMIPermissions().getAllowed().size()>0){
					if(!isVehicleDataSubscribed){ //If we haven't already subscribed we will subscribe now
						//TODO: Add the vehicle data items you want to subscribe to
						//proxy.subscribevehicledata(gps, speed, rpm, fuelLevel, fuelLevel_State, instantFuelConsumption, externalTemperature, prndl, tirePressure, odometer, beltStatus, bodyInformation, deviceStatus, driverBraking, correlationID);
						proxy.subscribevehicledata(false, true, rpm, false, false, false, false, false, false, false, false, false, false, false, autoIncCorrId++);
					}
				}
			}
		}
		*/
    }

	@Override
	public void onButtonPressResponse(ButtonPressResponse response) {
		try {
			Log.i(TAG, "REMOTE CONTROL ButtonPressResponse from SDL: " + response.serializeJSON());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSetInteriorVehicleDataResponse(SetInteriorVehicleDataResponse response) {
		try {
			Log.i(TAG, "REMOTE CONTROL SetInteriorVehicleDataResponse from SDL: " + response.serializeJSON());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onGetInteriorVehicleDataResponse(GetInteriorVehicleDataResponse response) {
		try {
			Log.i(TAG, "REMOTE CONTROL GetInteriorVehicleDataResponse from SDL: " + response.serializeJSON());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onOnInteriorVehicleData(OnInteriorVehicleData response) {
		try {
			Log.i(TAG, "REMOTE CONTROL OnInteriorVehicleData from SDL: " + response.serializeJSON());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

    @Override
	public void onSendHapticDataResponse(SendHapticDataResponse response) {
		
	}

    @Override
    public void onListFilesResponse(ListFilesResponse response){
        Log.i(TAG, "onListFilesResponse response from SDL: " + response);
    }

    @Override
    public void onSubscribeWayPointsResponse(SubscribeWayPointsResponse response){
        Log.i(TAG, "onSubscribeWayPointsResponse response from SDL: " + response);
    }

    @Override
    public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse response){
        Log.i(TAG, "onSubscribeVehicleDataResponse response from SDL: " + response);
    }

    @Override
    public void onPutFileResponse(PutFileResponse response){
        Log.i(TAG, "onPutFileResponse response from SDL: " + response);
    }

    @Override
    public void onOnLockScreenNotification(OnLockScreenStatus notification){
        Log.i(TAG, "onOnLockScreenNotification notification from SDL: " + notification);
        if(notification.getHMILevel() == HMILevel.HMI_FULL && notification.getShowLockScreen() == LockScreenStatus.REQUIRED) {
            Intent showLockScreenIntent = new Intent(this, LockScreenActivity.class);
            showLockScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(showLockScreenIntent);
        }else{
            sendBroadcast(new Intent("CLOSE_LOCK_SCREEN"));
        }
    }

    @Override
    public void onOnVehicleData(OnVehicleData notification){
        Log.i(TAG, "onOnVehicleData notification from SDL: " + notification);
    }

    @Override
    public void onGetWayPointsResponse(GetWayPointsResponse response){
        Log.i(TAG, "onGetWayPointsResponse response from SDL: " + response);
    }

    @Override
    public void onAddCommandResponse(AddCommandResponse response){
        Log.i(TAG, "onAddCommandResponse response from SDL: " + response);
    }

    @Override
    public void onOnWayPointChange(OnWayPointChange notification){
        Log.i(TAG, "OnWayPointChange response from SDL: " + notification);
    }
    @Override
    public void onUnsubscribeWayPointsResponse(UnsubscribeWayPointsResponse response){
        Log.i(TAG, "onUnsubscribeWayPointsResponse response from SDL: " + response);
    }

    @Override
    public void onAddSubMenuResponse(AddSubMenuResponse response) {
        Log.i(TAG, "AddSubMenu response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onCreateInteractionChoiceSetResponse(CreateInteractionChoiceSetResponse response) {
        Log.i(TAG, "CreateInteractionChoiceSet response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onAlertResponse(AlertResponse response) {
        Log.i(TAG, "Alert response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onDeleteCommandResponse(DeleteCommandResponse response) {
        Log.i(TAG, "DeleteCommand response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onDeleteInteractionChoiceSetResponse(DeleteInteractionChoiceSetResponse response) {
        Log.i(TAG, "DeleteInteractionChoiceSet response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onDeleteSubMenuResponse(DeleteSubMenuResponse response) {
        Log.i(TAG, "DeleteSubMenu response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onPerformInteractionResponse(PerformInteractionResponse response) {
        Log.i(TAG, "PerformInteraction response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onResetGlobalPropertiesResponse(
            ResetGlobalPropertiesResponse response) {
        Log.i(TAG, "ResetGlobalProperties response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse response) {
        Log.i(TAG, "SetGlobalProperties response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse response) {
        Log.i(TAG, "SetMediaClockTimer response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onSpeakResponse(SpeakResponse response) {
        Log.i(TAG, "SpeakCommand response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onOnButtonEvent(OnButtonEvent notification) {
        Log.i(TAG, "OnButtonEvent notification from SDL: " + notification);
    }

    @Override
    public void onOnButtonPress(OnButtonPress notification) {
        Log.i(TAG, "OnButtonPress notification from SDL: " + notification);
    }

    @Override
    public void onSubscribeButtonResponse(SubscribeButtonResponse response) {
        Log.i(TAG, "SubscribeButton response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse response) {
        Log.i(TAG, "UnsubscribeButton response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }


    @Override
    public void onOnTBTClientState(OnTBTClientState notification) {
        Log.i(TAG, "OnTBTClientState notification from SDL: " + notification);
    }

    @Override
    public void onUnsubscribeVehicleDataResponse(
            UnsubscribeVehicleDataResponse response) {
        Log.i(TAG, "UnsubscribeVehicleData response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

    }

    @Override
    public void onGetVehicleDataResponse(GetVehicleDataResponse response) {
        Log.i(TAG, "GetVehicleData response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

    }

    @Override
    public void onReadDIDResponse(ReadDIDResponse response) {
        Log.i(TAG, "ReadDID response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

    }

    @Override
    public void onGetDTCsResponse(GetDTCsResponse response) {
        Log.i(TAG, "GetDTCs response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

    }


    @Override
    public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse response) {
        Log.i(TAG, "PerformAudioPassThru response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

    }

    @Override
    public void onEndAudioPassThruResponse(EndAudioPassThruResponse response) {
        Log.i(TAG, "EndAudioPassThru response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

    }

    @Override
    public void onOnAudioPassThru(OnAudioPassThru notification) {
        Log.i(TAG, "OnAudioPassThru notification from SDL: " + notification );

    }

    @Override
    public void onDeleteFileResponse(DeleteFileResponse response) {
        Log.i(TAG, "DeleteFile response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

    }

    @Override
    public void onSetAppIconResponse(SetAppIconResponse response) {
        Log.i(TAG, "SetAppIcon response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

    }

    @Override
    public void onScrollableMessageResponse(ScrollableMessageResponse response) {
        Log.i(TAG, "ScrollableMessage response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

    }

    @Override
    public void onChangeRegistrationResponse(ChangeRegistrationResponse response) {
        Log.i(TAG, "ChangeRegistration response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

    }

    @Override
    public void onOnLanguageChange(OnLanguageChange notification) {
		try {
			Log.i(TAG, "OnLanguageChange notification from SDL: " + notification.serializeJSON().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

    @Override
    public void onSliderResponse(SliderResponse response) {
        Log.i(TAG, "Slider response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

    }


    @Override
    public void onOnHashChange(OnHashChange notification) {
        Log.i(TAG, "OnHashChange notification from SDL: " + notification);

    }

    @Override
    public void onOnSystemRequest(OnSystemRequest notification) {
        Log.i(TAG, "OnSystemRequest notification from SDL: " + notification);

    }

    @Override
    public void onSystemRequestResponse(SystemRequestResponse response) {
        Log.i(TAG, "SystemRequest response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

    }

    @Override
    public void onOnKeyboardInput(OnKeyboardInput notification) {
        Log.i(TAG, "OnKeyboardInput notification from SDL: " + notification);

    }

    @Override
    public void onOnTouchEvent(OnTouchEvent notification) {
        Log.i(TAG, "OnTouchEvent notification from SDL: " + notification);

    }

    @Override
    public void onDiagnosticMessageResponse(DiagnosticMessageResponse response) {
        Log.i(TAG, "DiagnosticMessage response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

    }

    @Override
    public void onOnStreamRPC(OnStreamRPC notification) {
        Log.i(TAG, "OnStreamRPC notification from SDL: " + notification);

    }

    @Override
    public void onStreamRPCResponse(StreamRPCResponse response) {
        Log.i(TAG, "StreamRPC response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

    }

    @Override
    public void onDialNumberResponse(DialNumberResponse response) {
        Log.i(TAG, "DialNumber response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

    }

    @Override
    public void onSendLocationResponse(SendLocationResponse response) {
        Log.i(TAG, "SendLocation response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

    }

    @Override
    public void onServiceEnded(OnServiceEnded serviceEnded) {

    }

    @Override
    public void onServiceNACKed(OnServiceNACKed serviceNACKed) {

    }

    @Override
    public void onShowConstantTbtResponse(ShowConstantTbtResponse response) {
        Log.i(TAG, "ShowConstantTbt response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

    }

    @Override
    public void onAlertManeuverResponse(AlertManeuverResponse response) {
        Log.i(TAG, "AlertManeuver response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

    }

    @Override
    public void onUpdateTurnListResponse(UpdateTurnListResponse response) {
        Log.i(TAG, "UpdateTurnList response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

    }

    @Override
    public void onServiceDataACK(int dataSize) {

    }

    @Override
    public void onOnDriverDistraction(OnDriverDistraction notification) {
        // Some RPCs (depending on region) cannot be sent when driver distraction is active.
    }

    @Override
    public void onError(String info, Exception e) {
    }

    @Override
    public void onGenericResponse(GenericResponse response) {
        Log.i(TAG, "Generic response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
	public void onGetSystemCapabilityResponse(GetSystemCapabilityResponse response){
		Log.i(TAG, "System Capability response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}



    /**
     * Helper method to take resource files and turn them into byte arrays
     * @param resource Resource file id.
     * @return Resulting byte array.
     */
    private byte[] contentsOfResource(int resource) {
        InputStream is = null;
        try {
            is = getResources().openRawResource(resource);
            ByteArrayOutputStream os = new ByteArrayOutputStream(is.available());
            final int bufferSize = 4096;
            final byte[] buffer = new byte[bufferSize];
            int available;
            while ((available = is.read(buffer)) >= 0) {
                os.write(buffer, 0, available);
            }
            return os.toByteArray();
        } catch (IOException e) {
            Log.w(TAG, "Can't read icon file", e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}