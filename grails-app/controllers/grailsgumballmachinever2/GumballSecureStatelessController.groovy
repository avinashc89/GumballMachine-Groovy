package grailsgumballmachinever2

import gumball.GumballMachine
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Mac
import java.security.InvalidKeyException
import java.lang.Object

class GumballSecureStatelessController {

    def String machineSerialNum = "1234998871109"
	def GumballMachine gumballMachine
	def String key ="huhg7695jbas479sdhfjd96wh88"
	def String sha256INSTANCE = "HmacSHA256"
	def String msg
	def Hash
	
	
	 def encryptData(String data) {
     try {
        Mac mac = Mac.getInstance(sha256INSTANCE)
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), sha256INSTANCE)
        mac.init(secretKeySpec)
        byte[] digest = mac.doFinal(data.getBytes())
        return digest.encodeBase64().toString()
       } catch (InvalidKeyException e) {
        throw new RuntimeException("Invalid key exception while converting to HMac SHA256")
      }
    }
	def index() {
		
		String VCAP_SERVICES = System.getenv('VCAP_SERVICES')
		
		if (request.method == "GET") {

			def gumball = Gumball.findBySerialNumber( machineSerialNum )
			if ( gumball )
			{
				gumballMachine = new GumballMachine(gumball.modelNumber, gumball.serialNumber)
				gumballMachine.setCount(gumball.countGumballs)
			}
			else
			{
				gumball = new Gumball()
				gumball.serialNumber = machineSerialNum
				gumball.modelNumber = "AS45DI9"
				gumball.countGumballs = 10 
				gumball.save(flush: true)
				gumballMachine = new GumballMachine(gumball.modelNumber, gumball.serialNumber)
				gumballMachine.setCount(10)
				
			}

			
			def ts = System.currentTimeMillis().toString();
			def gumDetails = gumballMachine.getCount()+"~~"+ gumballMachine.getCurrentState() + "~~" + gumballMachine._model_number + "~~" + gumballMachine._serial_number + "~~" + ts
			flash.encrypt = encryptData(gumDetails+key)
			flash.message = gumballMachine.getAbout()
			flash.gumDetails = gumDetails

			// display view
			render(view: "index")
			
		}
		else if (request.method == "POST") {

			def gumDetails = params?.gumDetails
			def message = params?.message
			def encrypt = params?.encrypt
			
			def(gumcount,state,modelNumber,serialNumber,prevts) = gumDetails.split("~~")
		
			def long ts = Long.parseLong( prevts )
			def long cts = System.currentTimeMillis()
			def long diffInSec = (cts - ts)/1000
		
			def newEncrypt = encryptData(gumDetails+key)
			
			
			if (diffInSec > 120 || !encrypt.equals(newEncrypt))
			{

				flash.gumDetails = gumDetails
				flash.message = "invalid session"
				render(view: "/error")
			}
			else 
			{
				gumballMachine = new GumballMachine(modelNumber, serialNumber) ;
				gumballMachine.setCurrentState(state);
				gumballMachine.setCount(Integer.parseInt(gumcount));

				if ( params?.event == "Insert Quarter" )
				{
					gumballMachine.insertCoin()
				}
				if ( params?.event == "Turn Crank" )
				{
					gumballMachine.crankHandle();

					if ( gumballMachine.getCurrentState().equals("gumball.CoinAcceptedState") )
					{
						def gumball = Gumball.findBySerialNumber( machineSerialNum )
						if ( gumball )
						{
							if ( gumball.countGumballs > 0)
							{
								gumball.countGumballs--;
								def count = gumball.countGumballs ;
								gumball.save(flush: true); 
								gumballMachine.setCount(count)
							}
							else
								gumballMachine.setCoinRejected();
						}
					}

				}
			
				def currTs = System.currentTimeMillis().toString();
				gumDetails = gumballMachine.getCount()+"~~"+gumballMachine.getCurrentState() + "~~" + gumballMachine._model_number + "~~" + gumballMachine._serial_number + "~~" + currTs
				flash.encrypt = encryptData(gumDetails+key)
				flash.message = gumballMachine.getAbout()
				flash.gumDetails = gumDetails

				render(view: "index")
			}
	}

}
}
