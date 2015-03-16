##my.guu.ru Timetable 

I study in State University of Management (SUM) in Moscow. We have one big problem - students schedule. Usually we have to try to find our schedule on http://lmsys.guu.ru/schedule/?schedule=session and it's hard, it's too complicated and not word properly.

The main idea of app is to download/sync and display students schedule. This app is for State University Of Management's students. 

Basic principles:

	1) SUM uses Microsoft Active Directory and Office 365 API to provide access to my.guu.ru, but Office 365 REST API is not stable (its just in preview) and SUM has limitations about using API, so we shouldn't use Office 365 REST API. However, we can use my.guu.ru API. 
	
	2) There are no logins and passwords (its not good idea to use Microsoft AD account), so I decided to use QR for user identification.
	
	3) Timetable updates everyday (no more), so we have to update data everyday.
	
	4) Timetable consists of classes. Every class contain a lot of information about student's group, classroom, professor. 
	

Every student should be able to get his (EXACTLY HIS) schedule with information about professor.

##Use case

    Step 0
        Download application [here!](https://docs.google.com/uc?id=0B_TTaqQQ7F7uLTF3Q2F6VG5ONGs&export=download) and install it on your device.
    Step 1
        Log in into my.guu.ru workspace with Microsoft credentials.  (see screnshots in Timetable App Folder on Google Drive, link is below )
    Step 2
         Select in menu "Mobile apps". (see screnshots in Timetable App Folder on Google Drive, link is below )
    Step 3
        Open application and press "Scan QR to log in ..."  (see screnshots in Timetable App Folder on Google Drive, link is below )
    Step 4
        Scan your QR (or QR below) and DONE! You'll see your timetable. You can navigate, see nearest classes, see information abour professors, share infromation about classes and you opinion about classes.
![QR CODE](https://chart.googleapis.com/chart?chs=220x220&cht=qr&chl=f2e8f695264e35ea8d43a36112fd69c62b33dc1a53ebd087174e7bcf043744ccb3eab19e3c216b842444d891084bcc1964fba9886daae0ba6505439d7cd7c7a7&choe=UTF-8)  


Mocks, screenshots, self-evaluation etc in Timetable App Folder on Google Drive:

https://drive.google.com/folderview?id=0B_TTaqQQ7F7ufkJUZWt2MG1acE9jN3JwclVObmV4cU9nWTQwaGQwa1liTFNKQ2NNZ29WTXc&usp=sharing
