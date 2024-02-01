import cv2
import numpy as np

import tkinter as tk
from tkinter import Label, Button, Scale, Frame
from PIL import Image, ImageTk
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing import image
from tensorflow.keras.applications.inception_v3 import preprocess_input



class WebcamApp:
    def __init__(self, window, window_title, model_path, labels):
        self.window = window
        self.window.title(window_title)

        self.model = load_model(model_path)
        self.labels = labels

        self.cap = cv2.VideoCapture(0)
        self.ROI_width = 200
        self.ROI_x, self.ROI_y = 50, 50

        self.table_height = 100
        self.table_y = 30

        self.video_source = 0
        self.vid = cv2.VideoCapture(self.video_source)

        self.frame = Frame(self.window, bg="#f0f0f0", padx=10, pady=10)
        self.frame.pack(expand=True, fill="both")

        self.canvas = tk.Canvas(self.frame, width=self.vid.get(cv2.CAP_PROP_FRAME_WIDTH), height=self.vid.get(cv2.CAP_PROP_FRAME_HEIGHT) + self.table_height, bg="#f0f0f0")
        self.canvas.pack()

        self.table_label = Label(self.frame, text="Predicted Text:", font=("Helvetica", 14), bg="#f0f0f0")
        self.table_label.pack(pady=5)

        self.predicted_text_label = Label(self.frame, text="", font=("Helvetica", 16, "bold"), bg="#f0f0f0", fg="#0066cc")
        self.predicted_text_label.pack(pady=5)

        self.btn_quit = Button(self.frame, text="EXIT", width=10, command=self.window.destroy, bg="#ff6666", fg="white", font=("Helvetica", 12, "bold"))
        self.btn_quit.pack(pady=10)


        self.update()
        self.window.mainloop()

    def update(self):
        ret, frame = self.cap.read()
        if ret:
            roi = frame[self.ROI_y: self.ROI_y + self.ROI_width, self.ROI_x: self.ROI_x + self.ROI_width]
            img = cv2.resize(roi, (150, 150))
            img_array = image.img_to_array(img)
            img_array = np.expand_dims(img_array, axis=0)
            img_array = preprocess_input(img_array)

            predictions = self.model.predict(img_array)
            predicted_label = self.labels[np.argmax(predictions)]

            cv2.rectangle(frame, (self.ROI_x, self.ROI_y), (self.ROI_x + self.ROI_width, self.ROI_y + self.ROI_width),
                        (255, 0, 0), 2)  # Always draw the blue box

            if np.max(predictions) > 0.5:  # You can adjust this threshold as needed
                cv2.putText(frame, f'Predicted: {predicted_label}', (10, self.table_y), cv2.FONT_HERSHEY_SIMPLEX, 1,
                            (0, 255, 0), 2, cv2.LINE_AA)

                self.predicted_text_label.config(text=f"Predicted Text: {predicted_label}")
            else:
                # Display "No hand sign detected" when no hand sign is detected
                cv2.putText(frame, 'Unkown', (10, self.table_y), cv2.FONT_HERSHEY_SIMPLEX, 1,
                            (0, 0, 255), 2, cv2.LINE_AA)
                self.predicted_text_label.config(text="Predicted Text: Blank")

            self.photo = self.convert_frame_to_image(frame)
            self.canvas.create_image(0, 0, image=self.photo, anchor=tk.NW)

        self.window.after(10, self.update)


    def convert_frame_to_image(self, frame):
        frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        return ImageTk.PhotoImage(image=Image.fromarray(frame))

if __name__ == "__main__":
    name = 'src/models/5th_Sign_model.h5'
    labels = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']

    root = tk.Tk()
    app = WebcamApp(root, "Webcam App", name, labels)