import grailsgumballmachinever2.Gumball

class BootStrap {

    def init  = { servletContext ->
		def GM = new Gumball(
			
			modelNumber: 'M102988',
			serialNumber:'1234998871109',
			countGumballs: 6
			)
		
		GM.save() 
    }
    def destroy = {
    }
}
 