<h1>RoboControl</h1>
Robo Control ist eine App, die es ermöglicht einen Roboter drahtlos zu steuern. Die Steuerung erfolgt entweder über einen virtuellen Joystick oder mit Hilfe des Gesture Control Armband MYO von Thalmic Labs.
 
Um Robo Control zur Robotersteuerung zu verwendet, muss das Smartphone mit dem WiFi-Netzwerk, welches z.B. von einem XBee® WiFi Modul von Digi International erzeugt wird, verbunden sein.
Bei korrekter Verbindung sendet Robo Control bis zu zehn Mal in der Sekunde Steuerdaten an das WiFi Modul, welche von einem Mikrocontroller ausgewertet und weiterverarbeitet werden können.

Um MYO für die Steuerung zu verwenden, wird Bluetooth Low Energy (BLE) benötigt. 

<h4>Installieren</h4>
1. Lade die aktuelle Version aus dem Ordner APK oder dem Dropbox Ordner (siehe Links) herunter.
2. Stelle sicher, dass du auf deinem Android-Smartphone die Option "Unbekannte Quellen zulassen" aktiviert ist
   <p>Um direkt in die Einstellungen für unbekannte Quellen zu gelangen, drücken Sie auf das Menü-Symbol oder auf die Menü-Taste auf der Startseite Ihres Gerätes und anschließend auf Einstellungen. Wählen Sie dann Anwendungen oder Sicherheit (abhängig von Ihrem Gerät) und schließlich Unbekannte Quellen aus.</p>
3. Installiere RoboControl

<h4>Erste Schritte</h4>

1. WiFi verbinden<br>
	Verbinden Sie ihr Smartphone mit dem WiFi-Modul.

	Robo Control -> WiFi Einstellungen<br>
	oder<br>
	Einstellungen (Smartphone) -> WLAN
	
2. MYO koppeln<br>
	Koppeln Sie MYO mit ihrem Smartphone per Bluetooth LE. Rufen Sie dazu die Einstellungen des Smartphones auf.

	Robo Control > BT Einstellungen<br>
	oder<br>
	Einstellungen (Smartphone) -> Bluetooth<br>
	Steuerung

3. Starten Sie nun Robo Control und passen Sie, wenn notwendig die Einstellungen an. Wählen Sie den gewünschten Control Modus aus.
	
Wenn Sie den Gesture Control Mode verwenden, wählen Sie im nächsten Dialog ihr Armband aus.

Die App wurde mit Eclipse und dem (veralteten) ADT Plugin programmiert. Um die App in Eclipse zu importieren, müssen vorher die beiden Projekte aus libs/Library.zip importiert werden. Danach kann RoboControl importiert werden. Evt. müssen die Bibliotheken neu zugewiesen werden.

<h4>Links</h4>

<b>Dokumentation und aktuelle APK:</b>
https://www.dropbox.com/sh/ppu5hy1env0s045/AACys7iwfQ-mSuf-PQVkKQX1a?dl=0<br />
<b>Arduino Roboter Sourcecode:</b> https://github.com/Xeratec/Roboter
<br />
<b>RoboControl in Action:<br />
[![RoboControl in Action](http://img.youtube.com/vi/zipDowhP6f8/0.jpg)](http://www.youtube.com/watch?v=zipDowhP6f8)

<h4>Play Store</h4>
RoboControl ist nun im Google Play Store erhältlich!<br />
[RoboControl - Google Play](https://play.google.com/store/apps/details?id=com.xeratec.robocontrol)
