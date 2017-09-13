import SocketServer


if __name__ == "__main__":
    # xbee stuffies (dispatch_async.py)
    from xbee import ZigBee
    from xbee.helpers.dispatch import Dispatch
    import time
    import serial

    PORT = 'COM7'
    BAUD_RATE = 9600

    global allRaceString
    allRaceString = ""

    # Open serial port
    ser = serial.Serial(PORT, BAUD_RATE)

    # Create handlers for various packet types
    buff = bytearray();
    global addrAll
    addrAll = []
    
    def status_handler(name, packet):
        if name == 'status':
            print "Status Update - Status is now: ", packet['status']
        else:
            print "Delivery Status is: ", chr(packet['deliver_status'])

    def io_sample_handler(name, packet):
        print "Samples Received: ", packet['samples']

    def rx_handler(name, packet):
        ##print "Data Received: ", packet['rf_data']
        for i in packet['rf_data']:
            buff.append(i)
        
    def all_handler(name, packet):
        print packet

    def at_response_handler(name, packet):
        print packet
        #We only care about the UUID.
        addr1 = bytearray()
        addr1.extend(packet['parameter'][2:10])
        global addrAll
        addrAll += [addr1]

    def remote_at_response_handler(name, packet):
        print packet

    def node_id_indicator_handler(name, packet):
        print packet


    # When a Dispatch is created with a serial port, it will automatically
    # create an XBee object on your behalf for accessing the device.
    # If you wish, you may explicitly provide your own XBee:
    #
    xbee = ZigBee(ser)
    dispatch = Dispatch(xbee=xbee) #We need a Zigbee object, so only this works
    #
    # Functionally, these are the same.
    #dispatch = Dispatch(ser) #Will not work for senior project.


    def tx_packet(source, data, packet=0, status = False):
        #Get the address of our sender
        send_addr_long = source['source_addr_long']
        send_addr = source['source_addr']
        
        xbee.tx(dest_addr=send_addr, #reply to sender
                dest_addr_long=send_addr_long,
                data=data,
                frame_id=chr(packet+1) if status else 0 )
        packet += 1
        #packetNum += 1

    def tx(source_long, source, data):
        
        xbee.tx(dest_addr=source,
                dest_addr_long=source_long,
                data=data,
                frame_id=chr(0))
        #packetNum += 1

    
    def sendAllZigbeeReq():
        xbee.send("at", command='ND')
        
    # Register the packet handlers with the dispatch:
    #  The string name allows one to distinguish between mutiple registrations
    #   for a single callback function
    #  The second argument is the function to call
    #  The third argument is a function which determines whether to call its
    #   associated callback when a packet arrives. It should return a boolean.
    dispatch.register(
        "status",
        status_handler, 
        lambda packet: packet['id']=='status'
    )

    dispatch.register(
        "tx_status",
        status_handler, 
        lambda packet: packet['id']=='tx_status'
    )

    dispatch.register(
        "io_data", 
        io_sample_handler,
        lambda packet: packet['id']=='rx_io_data'
    )

    dispatch.register(
        "recieve", 
        rx_handler,
        lambda packet: packet['id']=='rx'
    )

    dispatch.register(
        "at_response", 
        at_response_handler,
        lambda packet: packet['id']=='at_response'
    )

    dispatch.register(
        "remote_at_response", 
        remote_at_response_handler,
        lambda packet: packet['id']=='remote_at_response'
    )

    dispatch.register(
        "node_id_indicator", 
        node_id_indicator_handler,
        lambda packet: packet['id']=='node_id_indicator'
    )

    ##dispatch.register(
    ##    "all", 
    ##    all_handler,
    ##    lambda packet: True
    ##)

    # Create API object, which spawns a new thread
    # Point the asyncronous callback at Dispatch.dispatch()
    #  This method will dispatch a single XBee data packet when called
    xbee = ZigBee(ser, callback=dispatch.dispatch)

    NORTH = 'N'
    SOUTH = 'S'
    EAST = 'E'
    WEST = 'W'

    # Do other stuff in the main thread
    move = NORTH
    
    # end xbee stuffies

    def getAllZigbee():
        global addrAll
        while len(addrAll) < 1:
            pass
        #Convert to a string:
        s = ''
        allList = []
        for i in range(0, len(addrAll)):
            for j in range(0, len(addrAll[i])):
                s += "%(i)01x" % {'i':addrAll[i][j]}
            allList += [s]
        return allList

    def getAllRaces():
    ##Here, every race name is followed by its settings, then players/cars, in the format
    ##raceName|maxPlayers|defOrient|player1=+=car1=+++=player2=+=car2///raceName...
        global allRaceString
        return allRaceString
        
    def addRace(test):
        global allRaceString
        allRaceString += test
        pass
    
class MyTCPHandler(SocketServer.StreamRequestHandler):
        
    def processInput(self, theInput):
        theOutput = ''

        resp = ''
        zigbee = None
        if theInput == 'N':
            resp = 'N'
        elif theInput == 'S':
            resp = 'S'
        elif theInput == 'E':
            resp = 'E'
        elif theInput == 'W':
            resp = 'W'
        elif theInput == '0':
            resp = '0'
        elif theInput == '1':
            resp = '1'
        elif theInput[0:6] == 'addRace':
            addRace(theInput[7:])
            print "adding race:", theInput[7:]
        elif theInput == 'getRaces':
            theOutput = getAllRaces()
            ##print theOutput
        elif theInput == 'getCars':
            zigbee = getAllZigbee()
            for UUID in zigbee:
                theOutput += UUID + '#'
            print "the string of UUID's sent is: '", theOutput, "'"

        if resp != '':
            tx('\x00\x00\x00\x00\x00\x00\x00\x00', '\x00\x00', resp)
        return theOutput

    def handle(self): #We expect each send to be oneshot
        # self.rfile is a file-like object created by the handler;
        # we can now use e.g. readline() instead of raw recv() calls
        self.data = self.rfile.readline().strip()
        print "{} wrote:".format(self.client_address[0])
        print self.data
        # Likewise, self.wfile is a file-like object used to write back
        # to the client
        wantSend = self.processInput(self.data)
        if wantSend != '':
            print "Sending '", wantSend, "' to client..."
            self.wfile.write(wantSend + '\n')
        else:
            print "No response needed."

    
if __name__ == "__main__":
    #HOST, PORT = "nerketur-lappy-win7", 8080
    HOST, PORT = "152.8.113.30", 8080

    # Create the server, binding to localhost on port 9999
    server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)

    sendAllZigbeeReq()
    zigbee = getAllZigbee()
    print repr(zigbee)

    # Activate the server; this will keep running until you
    # interrupt the program with Ctrl-C
    server.serve_forever()
