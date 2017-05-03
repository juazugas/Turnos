<h1>Turnos Application</h1>

Turnos is a mobile application to inform about the Mexican support shifts of the development team.

<h2>Description</h2>

It synchronizes data directly from Google Sheet with code <sheet_code>, applying the synchronized pattern
of android development.

Implementing the sync adapter, checks once a day content from internet.

<h2>To Do</h2>

- Preferences activity with posibility of change (google document key identifier, period of synchronization, user identifier) and
  reading of preferences (last sync date, las sync status). Extra: show images in all shifts (boolean, default false), show month headers (boolean, default false),
  show previous shifts (boolean, default true).
- Horizontal views. Suggerence: StaggeredGridLayout.

For next versions :

- Navigation drawer with pre-filter options : my shifts, all shifts, by month (3 rounding months).
  (https://medium.com/android-news/navigation-drawer-styling-according-material-design-5306190da08f#.oud0h1qoa)
- Add a month header to the shift list.


<h2>Note</h2>

Please contact me to get the key for access to google sheet.
Put it on your gradle.properties in the .gradle directory on your home directory under the variable MyTurnosGoogleSheetKey.

Or create your own sheet with the next structure:

- Page 1.<br/>
year	week	date	sprint	EU	MX1	MX2<br/>
2015	1	29/12/2014	#1	waldo	geraldo	faldo<br/>
2015	2	5/01/2015	#1	fulano  mengano	zutano<br/>
- Page 2.<br/>
login	name	alias	position<br/>
fulano	Fulano Mengano Zutano	full	Sofware engineer<br/>
waldo	Waldo Geraldo Faldo	wally	Sofware engineer<br/>

<h2>Changes</h2>

2016-05-01

- Show synchronization errors with snackbar pattern (http://www.materialdoc.com/snackbar/).
- Check on start if the last synchro failed, inform and then launch sync again.

2016-03-13

- Cancel click on previous shifts.
- Hide 'current shift' button when searching.
- Update project to Android Studio 2.1 and prepare for 'Instant run' feature.

2016-01-10

- Refresh action from the menu in each activity. (http://developer.android.com/intl/es/training/sync-adapters/running-sync-adapter.html#RunPeriodic)
- Remove references to google spreadsheet.
- New menu options to refresh content and show current support shift.

2016-01-07

- Change reading the shifts from the Google sheet document like the team list.
- New Card view to show the current support week.


