# Viktiga importer..
import cv2
import os

# generate_data.py
# Användes för att skapa dataset bilderna


# Hitta mappen där den här filen ligger
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

CLIPS_DIR  = os.path.join(BASE_DIR, 'clips')
DATA_DIR   = os.path.join(BASE_DIR, 'data', 'SSL-dataset')

categories = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
              'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
              'V', 'W', 'X', 'Y', 'Z']

datasetIDsFirst = ['1G', '2G', '1J', '2J', '3J']
datasetIDsSecond = ['1K', '1P', '1S']

# Skapar data bilderna från kameran när programmet är igång.
def createImagesFromClip(letter, datasetID):
    folderPath = os.path.join(CLIPS_DIR, datasetID, letter)
    if not os.path.isdir(folderPath):
        raise FileNotFoundError(f"Kunde inte hitta bokstavskatalog: {folderPath}")

    for filename in os.listdir(folderPath):
        clipPath = os.path.join(folderPath, filename)
        videoStream = cv2.VideoCapture(clipPath)
        currentframe = 1
        counter = 0

        while currentframe <= 200 and videoStream.isOpened():
            ret, frame = videoStream.read()
            if not ret:
                break

            # Välj underkatalog: train/validation/test
            if currentframe <= 100:
                split = 'train'
            elif currentframe <= 150:
                split = 'validation'
            else:
                split = 'test'

            targetDir = os.path.join(DATA_DIR, split, letter)
            os.makedirs(targetDir, exist_ok=True)

            filename_out = f"{letter}{datasetID}_{currentframe:03d}.jpg"
            outPath = os.path.join(targetDir, filename_out)
            print(f"Creating {outPath}")

            frame = frame[0:1080, 200:960]
            frame = cv2.resize(frame, (224, 224))
            cv2.imwrite(outPath, frame)

            counter += 3
            videoStream.set(cv2.CAP_PROP_POS_FRAMES, counter)
            currentframe += 1

        videoStream.release()
        cv2.destroyAllWindows()

# kanske bör köra båda?
for id in datasetIDsFirst:
    for letter in categories:
        createImagesFromClip(letter, id)
        print('Done with', letter)
    print('Done with', id)

