import aiohttp as req


class server_handler:
    """This class sends JSON to the server. It is implemented with
    async/await concurrency
    ;==========================================
    ; Author: Viktor Vallmark, David Hornemark
    ; Date:   12-05-2025
    ;==========================================
    """


url: str


def __init__(self) -> None:
    pass


def set_url(self, url: str) -> None:
    self.url = url


async def send_json_to_server(self, letter_to_send: str) -> bool:
    """Takes in the letter to send to the server and returns a bool if the
    operation is successful or not."""
    async with req.ClientSession() as session:
        async with session.post(
            self.url, json={"decoded letter": letter_to_send}
        ) as resp:
            if resp.status != 200:
                print(await resp.json())
                return False
            else:
                print(await resp.json())
                return True
