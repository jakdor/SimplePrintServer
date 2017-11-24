# SimplePrintServer
SimplePrintServer (working name) [Java][Desktop]

Project status: alpha1.0.2

### About

Main purpose of this project is to create alternative system for LAN printing, by
removing need to install printer drivers on client side. It can be a viable solution
when drivers for printers are not available for client OS but working on host/server
PC.

Project also aspires to provide stable and reliable data transfer architecture
as a alternative to Samba based Windows architecture suffering from connection issues.

### Installation

* Install Java 1.8 or newer
* Copy .jar files to any location
* On Linux mark .jar as executable
* Server.jar needs to by running on PC with connected printer
* Alternatively copy server.jar to start-up folder or add lunch script
* Lunch client.jar on PC you want to print from

### Usage

* Provide client app with server ip and port (if changed)
* Drag&drop or select file you want to print
* Choose print profile & mode
* Send print task or file to server

###### Print Modes

* Default fast print - lunch print profile print command on temporary file
* Open before printing - lunch print profile open command on saved file
* Send file only - saves received file, shows received prompt on server PC

### Print Profiles

Print profiles provides additional commands allowing to automate printing process.

Syntax:

```
%FILE% - replaced with print file

Name - profile name
Print command - print application command
Open command - open application command (write only %FILE% for system default app)
File format - file format separated by commas or spaces
```

Example:

```
Name - Word
Print command - <MS Office Word path> %FILE% /q /n /mFilePrintDefault /mFileExit
Open command - <MS Office Word path> %FILE%
File format - docx, doc, odt, rtf
```
