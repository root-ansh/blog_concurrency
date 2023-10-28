# Hello Concurrency

1. native java/android solutions: threads, executors, loopers, asynctasks, etc : https://docs.google.com/document/d/1Ls7YmfzDI_TkIxCIybXlHUQxmBwqDM7a/edit#heading=h.gjdgxs 
2. coroutines : https://docs.google.com/document/d/1J-MDwbXV36XLB865mDu2X7T5ArX0MkmcNJes4XW6JwE/edit#heading=h.x5q5ltn9boih
3. flows : https://docs.google.com/document/d/1O58up-0lsctgzM7y6Ylj_szfmsDn7yFyaEqaTSdBPLg/edit
4. rx : in this repo

Concurrency Scenarios:
- Basic Api Request Response
- Buffering/Streaming
- Android : Press twice to exit/do an action action
- Throttling : Search on edit text keypress
- Debouncing : cancel multiple clicks on a button / pull to refresh
- Async Await
- Multi Requests: 
  - made x requests at one go, 1 request failed -> cancel all the x requests / make x-1 another requests
  - made x requests at one go, 1 request failed -> ignore and do task y( which is supposed to happen on completion of all x requests)
  - made x requests at one go, 1 request failed -> retry 3 times for request after which do scenario 1 or 2
