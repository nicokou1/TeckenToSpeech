# Tecken to speech
  @author Nicolas K.

Group Fjälluggla's project for the course DA393A - Projektarbete.

This system translates swedish sign language in realtime using machine-learned models
to predict letters shown on a camera.

By using TCP-oriented http protocols letters get sent to our server that stores them in a
queue using LIFO structure, which in turn gets popped from our client mobile app.

Using an embedded system with an external camera mounted on enables showing sign language
which our models pick up and analyze, resulting in a predicted letter. The user gets to store
the chosen letters and sends them to the server.

Our mobile application allows for real-time updates from the server, fetching letters
when instructed to by the user. The fetched letters can be read out loud if the user chooses to do so.
Either way the fetched letters appear on the mobile screen.