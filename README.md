Hi, this is my MC monitor. I made a simple Python app using webview.
I also created a small local web server with http.server, so I can load my own HTML page (index.html).

The server runs on 127.0.0.1:8000 in a separate thread, so it doesn’t block the app.
Then I open a window using webview, which displays the page from the local server.

Basically, it works like a lightweight desktop app that shows my web interface.
