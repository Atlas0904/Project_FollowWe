## Project_FollowWe
This Project is designed to facilate you and your friend as traveling.
User can check where your friends are. This willl benifit you  when all of you during a trip.

Moreover, user can set destination to everyone. That is, all of you can know where to go without using phone call or other IM.

#Feature
1. Show all user who in the same room
2. User can choose their own icon & name
3. User can share destination to others
4. User can add markers's in Google map (Clound sync)
5. User can save current user route and reloaded
6. User can enable background services to update location to others
7. User can lock current camera to facilate check rather than scoll screen
8. For users in the same room, they are be avilable to chat with each others
9. User can chat with other users who login on map

#ToDO
1. Password check
2. Optimize user route
3. Optimize the algorithm for walking, driving
4. Let user to choose arrived type
5. Need to beatify chat room UI
6. Hiking app?

#Optimize
1. Distance part need to be more precise

Component:

Android permission:
android.permission.ACCESS_FINE_LOCATION

Backend:
Firebase 

Icon and picture Resource from:
All icon contributed  by flaticon.com Under CC: Freepik. Designed by Freepik and distributed by Flaticon

#Version
2016/09/07: Adjust layout
2016/09/05: Add One2One chat feature. Refactor using ChatValueEventListener.
2016/09/02: Share action bar
2016/09/02: Complete group chat room part. Use BaseValueEventListener as a general Firebase element return
2016/09/01: Start to implement Group chat room (Need to complete Firebase part & Chatroom UI part)
2016/08/29: Fix floating button click when loading not complete. Use getAddress() rather than getName() for PlaceSelectionListener. class Place Seriablizable
2016/08/29: Feature: save user path you can check what you been ever
2016/08/29: Use README file to record the change as will

#Refactor
Need to refactory MapsActivity -> MapFragement

By Atlas