import requests
import base64
class PythonClient:
    def __init__(self, url:str) -> None:
        self.url=url
    def post_image(self, pngImagePath:str)->str:
        with open(pngImagePath, 'rb') as imageFile:
            image = 'data:image/png;base64,'+base64.b64encode(imageFile.read()).decode("utf8")
            resp = requests.post(self.url, data=image)
            message=resp.text
            return message