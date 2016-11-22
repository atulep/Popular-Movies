# 11/18/16
Checked database, and it looks like the files are in there. But, they are
not being displayed in the app.
Issue: Detail URI is null. -- fixed
# 11/19/2016
1. The MovieId is being printed as 0 in the TrailerFragment .... (why)
2. The Cursor that was passed to the DetailFragment was null. <-- because for certain movies app crashes because there are no reviews available
# 11/21/2016
1. Determined the source of error with the null cursor. Need to fix by catching an exception.
2. Looks like Fetch tasks are being called many times every time I click on the icon.
3. The trailers are not showing up, though they are present in the db. <-- fixed 
