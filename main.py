import webview
import threading
import http.server
import socketserver

PORT = 8000

class Handler(http.server.SimpleHTTPRequestHandler):
    def log_message(self, format, *args):
        pass  # vypne logy

def start_server():
    with socketserver.TCPServer(("127.0.0.1", PORT), Handler) as httpd:
        httpd.serve_forever()

threading.Thread(target=start_server, daemon=True).start()

webview.create_window("Trixx ukradl mé babičce umyvadlo a já to vím", f"http://127.0.0.1:{PORT}/index.html", width=1000, height=700)
webview.start()