# MyBookInventory
Book Store Inventory App
Udacity Android Basics by Google Nanodegree Scholarship 2017/2018
#madewithudacity #googleudacityscholars #abndproject

project 8 InventoryApp Stage1 - project 9 InventoryApp Stage2

This app store in a SQLite database and display a list of book ans some of their feature, such as the title (product_name), price, quantity, name of the book supplier, phone number of supplier and whether or not one book is out of print.

In the first activity, the BookListActivity, either a list of books (title, priceand quantity) is displayed or an empty view, if there are no books stored into the database. In each list item there is a button, when it gets clicked the quantity of books decreases by one. By clicking the FAB in the main activity the AddBookActivity opens; from here it is possible to save a new book record, by giving the required information and then clicking the FAB. A new book list item will appear in the BookListActivity.
By clicking a list item in the BookListactivity the BookDatailActivity opens,from here it is possible to call the book supplier or to delete the current book entry or to open the EditBookActivity where the information on the book can be updated.

I integrated Butter Knife library to the code for easy initialization by eliminating the use of findViewById. Butter Knife Copyright 2013 Jake Wharton http://jakewharton.github.io/butterknife/

API 27
As Loader are deprecated in API 28 I chose to revert to API 27.

Phone number either invented or generated on:
https://fakenumber.org/

FAB
https://developer.android.com/guide/topics/ui/floating-action-button

SPINNER
https://developer.android.com/reference/android/widget/Spinner


UriMatcher
https://gist.github.com/udacityandroid/0c837f16337c46baeaba89d680dc0254
used some of the code documentation of this gist into my code.

OnItemClickListener in ListView item containing button:
https://stackoverflow.com/questions/8413656/onitemclicklistener-doesnt-work-with-listview-item-containing-button
https://stackoverflow.com/questions/6703390/listview-setonitemclicklistener-not-working-by-adding-button

To get the sale button work in all of the list item view:
https://stackoverflow.com/questions/28492493/android-custom-listview-cursoradapter-updating-the-last-item

Passing data between activities 
https://www.101apps.co.za/articles/passing-data-between-activities.html

How to pass a URI to an intent?
https://stackoverflow.com/questions/8017374/how-to-pass-a-uri-to-an-intent
https://developer.android.com/reference/android/content/Intent

Intent Phone call
https://developer.android.com/guide/components/intents-common#Phone 


In building this app I relied on the Udacity Android Basics by Google Nanodegree Program Data Storage Course lessons.

Sample book:
Il manuale del fitopreparatore, bruno Pelle, Studio Edizioni, 1998, p.211, Fuori Catalogo € 30.0
