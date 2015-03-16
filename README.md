##my.guu.ru Timetable 

I study in State University of Management (SUM) in Moscow. We have one big problem - students schedule. Usually we have to try to find our schedule on http://lmsys.guu.ru/schedule/?schedule=session and it's hard, it's too complicated and not word properly.

The main idea of app is to download/sync and display students schedule. This app is for State University Of Management's students. 

Basic principles:

	1) SUM uses Microsoft Active Directory and Office 365 API to provide access to my.guu.ru, but Office 365 REST API is not stable (its just in preview) and SUM has limitations about using API, so we shouldn't use Office 365 REST API. However, we can use my.guu.ru API. 
	
	2) There are no logins and passwords (its not good idea to use Microsoft AD account), so I decided to use QR for user identification.
	
	3) Timetable updates everyday (no more), so we have to update data everyday.
	
	4) Timetable consists of classes. Every class contain a lot of information about student's group, classroom, professor. 
	
	
Every student should be able to get his (EXACTLY HIS) schedule with information about professor.
