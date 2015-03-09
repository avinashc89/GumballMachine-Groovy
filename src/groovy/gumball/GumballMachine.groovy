package gumball

class GumballMachine {
	
	def  _model_number
	def  _serial_number
	def IGumballState _current_state 
	def  count
	
	NoCoinState _no_coin = new NoCoinState(this)
	CoinAcceptedState _coin_accepted = new CoinAcceptedState(this)
	CoinRejectedState _coin_rejected = new CoinRejectedState(this)
	HasCoinState _has_coin = new HasCoinState(this)
	
	GumballMachine(model, serial)
	{
		_current_state = _no_coin
		_model_number = model
		_serial_number = serial
	}
	
	def insertCoin()
	{
		_current_state.insertCoin()
		
	}
	
	def crankHandle()
	{
		_current_state.crankHandle()
	}
	
	def setNoCoin() { _current_state = _no_coin }
	def setCoinAccepted() { _current_state = _coin_accepted }
	def setCoinRejected() { _current_state = _coin_rejected }
	def setHasCoin() { _current_state = _has_coin }
	def String getCurrentState() { return _current_state.getClass().getName() }

	def setCurrentState(state)
	{
		if (state.equals("gumball.NoCoinState")) { setNoCoin() ; }
		if (state.equals("gumball.CoinAcceptedState")) { setCoinAccepted() ; }
		if (state.equals("gumball.CoinRejectedState")) { setCoinRejected() ; }
		if (state.equals("gumball.HasCoinState")) { setHasCoin() ; }
	}

	
def String getAbout() {
		return """
--------------------------------------------------
Mighty Gumball, Inc.
Groovy-Enabled Standing Gumball
Model# ${_model_number}
Serial# ${_serial_number}
--------------------------------------------------
Current State: ${_current_state.toString()}
No of gumballs: ${count}
"""		
}

public int getCount() {
	return count;
}

public void setCount(int count) {
	this.count = count;
}


	
}
