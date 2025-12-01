## ProjectScramble

**Description**
Java command-line (CLI) program designed for encrypting (visual scrambling) and decrypting video files. The goal is to secure the visual content of a video using an algorithmic process `((r + (2s+1)idLigne)%size)`, while maintaining the ability to restore the original stream.

**Technologies Used**
* **Language:** Java
* **Image/Video Processing:** OpenCV

**Key Features**
* Video file import.
* Video Encryption: Application of a scrambling algorithm to render the video unreadable.
* Video Decryption: Restoration of the original video from the encrypted file.
* Command-line interface to control the process.

**Demo**
The project in action:

Cyphered video :  
![Encryption Demo](https://github.com/Quentin-BELHADJ/ProjectScramble/raw/main/medias/scrambled_test.gif)

---

Decyphered video :  
![Decryption Demo](https://github.com/Quentin-BELHADJ/ProjectScramble/raw/main/medias/unscrambled_test.gif)

---

### Technical Details & Feedback

**Challenges Faced**
* **Performance:** Handling image quality issues related to the MJPG codec usage during frame-by-frame processing.

**Future Improvements**
* Implementation of keyless decryption.
* Development of a Graphical User Interface (GUI) using JavaFX.
