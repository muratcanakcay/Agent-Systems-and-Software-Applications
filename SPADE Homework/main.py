import time
from playerAgent import Player
from spyAgent import Spy

if __name__ == "__main__":
    player1 = Player("mca@shad0w.io/1", "12345678")
    player1.set("name", "1")
    player1.set("otherPlayerName", "2")

    future = player1.start()
    future.result()

    player2 = Player("mca@shad0w.io/2", "12345678")
    player2.set("name", "2")
    player2.set("otherPlayerName", "1")
    player2.start()

    spy = Spy("mca@shad0w.io/3", "12345678")
    spy.start()

    while player1.is_alive() or player2.is_alive():
        try:
            time.sleep(1)
        except KeyboardInterrupt:
            player1.stop()
            player2.stop()
            spy.stop()
            break
    
    spy.stop()    
    print("Agents finished")