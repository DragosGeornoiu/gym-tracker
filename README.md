# Gym Workout Tracker

A simple web app to track and log your workouts. It allows you to select exercises, add sets with weight and reps, and commit the workout data to a GitHub repository. The data is stored in JSON files within the repo.

# Github pages

This project is hosted on GitHub Pages and can be accessed at the following URL:
[https://dragosgeornoiu.github.io/gym-tracker/](https://dragosgeornoiu.github.io/gym-tracker/)


## Installing app

npm install

## Running app locally

npm run dev

## Notes

* using tutorial for dynamic table edits: https://muhimasri.com/blogs/react-editable-table/
* 

# Known Bugs/Future Enhancements

* On Muscles Page, after adding a new item (muscle), if you try to edit that item and cancel the edit, it will throw an error which breaks the page. Same thing happens for exercises page
* error handling/displaying when adding a new item (muscle/exercise) is not clear, it's not clear that everything from there is mandatory. The header for the popup is to generic, it should mention what exactly is being added.
* id generation will have issue in the current format, especially when deleting previous entries. It should be based on the highest id maybe, and increment that, but not sure what happens when deleting the highest id, previous entries will reference the wrong id.
* handling for deleting a muscle reference in exercises is not present. Maybe this should not be allowed until you remove the references to it or maybe it should ask for an extra confirmation before deleting also the previous entries, but that seems bad. Maybe something like a deactivation?! 
* entire look&feel of the project needs to be enhanced
* For exersises page we need images to be added. Not sure how to handle these, if an url or the image should be commited and saved.