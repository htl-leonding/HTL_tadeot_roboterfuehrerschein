# Roboter Führerschein (Tag der offenen Tür)
Dieses Repository umfasst alle wichtigen Infos sowie den Server und den Client für das Projekt **HOTRoad** (_Himbeerkuchen on the Road_) welches am Tag der offenen Tür unter dem Namen **Roboter Führerschein** vorgeführt wird.

### Installation Guide
**WARNING**: This guide assumes you have a Raspberry Pi (Model B or newer/better) with Raspbian OS installed and 2 free USB ports. The Raspberry Pi must be connected to the internet and you’ll need to SSH onto it. You should also know how to gain superuser rights, because they are a necessity throughout the guide.

1. **Assembly of the car**<br>
    Use the parts from the given list (_“Stückliste.xlsx”_) and assemble them as the plan (_“Schaltplan.png”_) says. (You can also use a EDIMAX WiFi adapter)
2. **Flash drive and copy files**<br>
    Copy the “hrinstall” folder from the Distribution directory onto a flash drive. When you’re done, plug the drive into your Raspberry Pi. You need to mount the flash drive with:
    ```
    $ mkdir /mnt/usb
	$ mount /dev/sda1 /mnt/usb/
    ```
    Copy the files from the flash drive with:
	```
	$ cp /mnt/usb/* /home/pi/
    ```
    Now unmount the flash drive with:
	```
	$ umount /mnt/usb/
	```
	
3. **Packages, packages, packages**<br>
    You now need to install a few packages, which are necessary for **HOTRoad** to even function. You’ll need:   
    
    ```
    $ apt-get install hostapd isc-dhcp-server libjpeg8-dev imagemagick libv4l-dev
    ```
    Now comes the fun part.
    
    
4. **Configuring hostapd**<br>
    Open up “/etc/hostapd/hostapd.conf” add the following lines:
    ```
    interface=wlan0
    driver=nl80211  **---> Use rtl871xdrv for EDIMAX adapters**
    ssid=HOTRoad
    hw_mode=g
    channel=11
    wpa=1 wpa_passphrase=himbeerkuchen
    wpa_key_mgmt=WPA-PSK
    wpa_pairwise=TKIP CCMP
    wpa_ptk_rekey=600
    macaddr_acl=0
    ```

    If you know how, change the settings appealing to your needs!
    Now open up **“/etc/default/hostapd”** and add:
	```
	DAEMON_CONF=”/etc/hostapd/hostapd.conf”
    ```
    
    **For EDIMAX adapters**<br>
    If you are using a EDIMAX adapter you need to change the hostapd binary. Copy the given hostapd binary with the following script:
	```
	 $ mv /usr/sbin/hostapd /usr/sbin/hostapd.bak
	 $ cp /home/pi/dat/hostapd /usr/sbin/
	```
	
5. **Configuring isc-dhcp-server**<br>
    Edit **“/etc/dhcp/dhcpd.conf”** and add the following:
	```
	subnet 10.10.0.0 netmask 255.255.0.0 {
    range 10.10.0.25 10.10.0.50;
    option domain-name-servers 8.8.4.4;
    option routers 10.10.0.1;
    interface wlan0;
    }
    ```

    Override the existing settings in **“/etc/network/interfaces”** with:
	```
	iface wlan0 inet static
	address 10.10.0.1
	netmask 255.255.255.0
	```
	
	
6. **Configuring mjpg-streamer**<br>
    You need to build the given version of mjpg-streamer (no really, only the given version is working – at least for me). To achieve this you need to follow these commands:
    ```
    $ cd /home/pi/mjpg-streamer
	$ ln –s /usr/include/linux/videodev2.h
    $ make mjpg_streamer input_file.so output_file.so
    ```

7. **Start scripts**<br>
    Everything is going well? This is the last step in this guide. You now need to tell the system to start everything when it boots. Create a file called **"hrinit.sh"** and add the following lines:
    ```
    #!/bin/sh
    sleep 15
    cd "/home/pi/mjpg-streamer/"
    sudo java -jar "home/pi/dist/HOTRoad.jar" & sudo mjpg_streamer -i "./input_uvc.so -n -y -f 15 -q 20 -r 320x240" -o "./output_http.so -n -w ./www -p 80"
    ```
    Save the file under **“/etc/init.d/”** and run:
	```
	$ update-rc.d /etc/init.d/hrinit.sh defaults
    ```
    Ignore the warnings: You are running a patchwork product.
    Now run these:
	```
	$ update-rc.d hostapd enable <br>
	$ update-rc.d isc-dhcp-server enable
    ```
### Good Job. You are done. Now restart your Raspberry Pi and get driving.


PS.: Addition from 2016
In the new pimped version you need to create a folder “/home/pi/music”. You also need to build the JARs yourself. The hostapd binary and the mjpg-streamer source code are under the folder “Other”.
