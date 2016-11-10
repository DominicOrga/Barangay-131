## Synopsis

**This software is designed for Windows OS only**

An information system application for the Barangay 131 at Caloocan City. The aim of this software is to build a functional application for taking resident photo, capturing digital signature, backup and restore data, and above all, to create an automated system for the production of Resident Barangay Information (RBI) such as the barangay clearance, barangay ID and business clearance, while providing a minimalistic and intuitive design.

## Dependencies

This software requires: MySQL as a RDBMS to store the resident records and the Java JDK.

## Installation

This software comes with an installer to setup the application as well as its dependencies. 

Installer Download Link (No Virus): https://drive.google.com/file/d/0B3FNs6U5Mw5mWWtaYjRmN3dhSmc/view

Requirements: Make sure that MySQL is not installed within the application before running the installer. If there is, then clean uninstall it.

Once all the requirements are satisfied, then you can freely execute the installer.exe.

**Installer Content**
	
	1. MySQL Ver 14.14 Distrib 5.7.15, for Win32 (AMD64)
	2. Bullzip PDF Printer
	3. Java JDK 8u101 (32-bit and 64-bit)
	4. Barangay131 Software

**The installer will accomplish the following:**
	  
	  i. Install MySQL in C:\
	 ii. Install Bullzip PDF Printer
	iii. Install appropriate Java JDK depending on the operating system.
	 iv. Place this application at Program Files\Barangay131
	  v. Create an application shortcut at the desktop

## Features
####Information System  
**Resident and Business Registration.** A resident and a business must be registered initially to be able to generate RBIs for a specified resident.  
**RBI Generation.** The system supports the creation of Barangay Clearance, Barangay ID and Business Clearance.  
**Fill out once, Generate Many.** Once a resident or a business has previously generated a certain RBI, the data is preserved and will be used to automatically fill out the next generation of the specified RBI.  

####Image Handling  
**Upload image.** The software has the capability to upload a profile photo or signature image.  
**Capture image.** The software can detect and launch an internal or external webcam to capture a profile photo or signature image, if such a peripheral is available within the computer.  
**Filter image.** The software has the ability to filter signature images, removing the light pixels of the image to create a transparent background, leaving the signature intact.  
**Crop Image.** The software has the capability to crop uploaded profile photos or signature images.  

####Data Storage  
**Primary Storage.** The software makes use of cached data to avoid constant connection with the database.  
**Secondary Storage.** MySQL serves as a container for the RBI records. Meanwhile, application preferences are stored in an Encrypted JSON file.  

####Security Mechanism
**Log in / Log out.** User authentication is implemented to access the system.  
**Automated Logout.** The system will log out automatically after a specified amount of time. Time limit can be modified at the security settings.  
**Secure Password Storage.** The password is stored in an AES encrypted JSON file.  

## Gif Demonstration
**Resident Registration**
![resident registration](https://cloud.githubusercontent.com/assets/12520299/20195048/e7df228e-a7cf-11e6-8f3b-24e6c1f6fd36.gif)

**Barangay ID Creation**
![barangay id](https://cloud.githubusercontent.com/assets/12520299/20195060/f0c4f554-a7cf-11e6-988e-963eb83a0af3.gif)

## License

This software is under the GNU General Public License. 
See LICENSE.txt
