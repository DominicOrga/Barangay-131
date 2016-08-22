[33mcommit 7714fbbd15c4786fc0fd05762c8687bf1ed76491[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Sun Aug 21 20:17:26 2016 +0800

    Clear the barangay ID details when no ID selection
    
    Also, Some minor bug fix.

[33mcommit f43a6ba954c0b0bc24ccc632ff32188dcabb7b18[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Sat Aug 20 21:38:24 2016 +0800

    Print barangay id report
    
    Bazeingga! Printing now works for barangay id. Cheers!

[33mcommit 70ad9e7822d82fb7f83d45e6dba2655e792a6f4c[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Sat Aug 20 09:27:26 2016 +0800

    Bug Fix

[33mcommit ea512192435481d865870ecf48b249ea2d22f070[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Sat Aug 20 06:15:53 2016 +0800

    Put details scenes directly inside information view
    
    It turns out that applying multiple fxml scenes with a single controller
    is not recommended, and I see why. The controller has an fx:id that
    can only cater to one fxml scene. The controller can only see the
    fxml components of one fxml scene and not the other ones.

[33mcommit df9eaac9ed7553d14b4a28361287d756e7987c91[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Thu Aug 18 19:35:58 2016 +0800

    Add Database Model create barangay id function

[33mcommit 3679a9618e121b11d900ed4ab4d18970bbc23e09[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Thu Aug 18 19:18:10 2016 +0800

    Remove Database Model copy file function
    
    This function is not needed.

[33mcommit 4366791f4a15ea46d3dd04219835ece7b93f9673[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Thu Aug 18 14:50:11 2016 +0800

    Create DraggableSignature class
    
    The DraggableSignature class transforms the signature views to be
    movable and resizeable during report creation.

[33mcommit eea83d016ad088c1d2fc4d48c313b1097d9db99f[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Thu Aug 18 10:10:46 2016 +0800

    Pass data from barangay id form to report
    
    Barangay ID data gathered from the form will be passed to the report
    to be displayed.

[33mcommit c2aa53d46f27b34259e23ed32180ad8a42ee1522[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Wed Aug 17 21:19:37 2016 +0800

    Create Barangay ID Report View
    
    JavaFX scene builder is buggy.
    
    The scene created directly from the JavaFX scene builder does not
    reflect the actual scene when staged from a java IDE.
    
    Some modifications are made to compensate for the
    buggy ui, such as with the TextArea wherein a workaround is needed
    to prevent the vertical scroll bar from showing.
    
    Furthermore, an ImageView holding an image outside the project
    will not be displayed unless done programmatically.

[33mcommit 7240afb31b33814ccaae638eca52bdfcb4d2dc4b[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Wed Aug 17 18:56:25 2016 +0800

    Add columns for the signature dimensions at Barangay ID
    
    During the creation of the barangay ID, the coordinates and dimension
    of the resident and chairman signatures. For this reason, it is
    necessary that we add columns to store the dimensions of the signatures.

[33mcommit 886e2b7aef7699306e3add997f5f347b7a1cae45[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Tue Aug 16 21:29:49 2016 +0800

    Update back and next page button

[33mcommit d52b0e8b9d22275e45f7ad6e254e209e66f1a24e[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Tue Aug 16 21:06:35 2016 +0800

    Finish Barangay ID form
    
    Finished the Barangay ID form. Next stop, the Barangay ID report.

[33mcommit 111d78b01899d71da80aef797519279087763c86[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Mon Aug 15 21:31:31 2016 +0800

    Make barangay id holder

[33mcommit 1adb60092fe93477f9c9ce3b2edfd6af12754ae8[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Mon Aug 15 21:17:40 2016 +0800

    Enable resident list at barangay ID form
    
    Duplicated the resident list paging algorithm from the resident control
    and transferred it to the barangay ID form to enable the resident list.
    However, it does not load the selected resident yet.

[33mcommit 40fdf798f9a6472fdd6f26716449523ade932ecc[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Mon Aug 15 16:30:55 2016 +0800

    Create Barangay ID form
    
    Alas, couldn't rely on JN to make a fucking form on time and not on time.

[33mcommit 3c2284669cb8645ef1da9f3b541ccb7961952a98[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Mon Aug 15 11:41:52 2016 +0800

    Update algorithm of DraggableRectangle resizing
    
    Resizing the rectangle has a faulty algorithm that changes its ratio.
    Introduced a new algorithm to maintain the ratio of the rectangle.

[33mcommit f9e2b6734b60cd370546d1c63f63fee051f53fe5[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Sun Aug 14 19:57:13 2016 +0800

    Fix bug
    
    Bug such as querying the resident essentials are only
    sorted by the last name, thus, neglecting the first and middle name.
    
    Also, some minor bug fixes.

[33mcommit b6de3e363e5499432c493403770afe33ec303610[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Sun Aug 14 17:36:52 2016 +0800

    Create CSS Contract
    
    CSS Contract will take hold of all string CSS Styles in order to
    prevent memory leaks.

[33mcommit c14100d6edbe2597fcf057c377ce8d960e217953[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Sat Aug 13 21:47:19 2016 +0800

    Display barangay ID record in list paging
    
    Barangay ID records are now displayed in the list paging of Barangay
    ID Control. I'm good.

[33mcommit 2ba86d6c68b826c9d99ea243fd8333130e44b27e[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Fri Aug 12 20:29:51 2016 +0800

    Prepare Barangay ID Scene creation
    
    Initializing initial requirements of the Barangay ID scene.
    Updated the database contract, database model and cache model to
    contain properties and methods necessary for the Barangay ID scene.

[33mcommit f6bf74a39b7ccefaed3d7214f8c913e93ead3524[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Fri Aug 12 08:29:52 2016 +0800

    Finish Barangay agent control
    
    Barangay agent control is not fully operational.
    Although the deadline is a little bit off... well at least I tried.

[33mcommit 8f49a0cd7b99a881808312f5ba47d05c00175508[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Thu Aug 11 18:04:55 2016 +0800

    Build add and remove button for kagawad holders
    
    Bazzeingga! I've done it!
    
    Pressing the add button will introduce another kagawad holder and
    pressing the remove button beside it will remove it.

[33mcommit 419e08067e9d8a8ad13f7bf90f5a988e6009e5d7[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Thu Aug 11 11:35:55 2016 +0800

    Store data as a JSON file with Preference Model class
    
    Hello, JSON! welcome to the club.

[33mcommit ad4cb0825d517405d9f780ed7d83370d7c871ada[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Thu Aug 11 06:19:54 2016 +0800

    Use JSON to store preferences rather than Preferences API
    
    The Preferences API is a good candidate to store setting
    preferences. However, the data is stored in the registers. Manual
    creation of registry keys are needed before the API can properly
    function, making it unsuitable for a system that will be handed
    to clients with no customer support.
    
    Making use of JSON as a workaround.

[33mcommit c9f3d593e999da14014b437252ed89cf725d4d01[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Wed Aug 10 11:57:58 2016 +0800

    Enable or disable web cam mirroring
    
    This is very useful for capturing signatures, since
    they are inverted by default.

[33mcommit e29f53e7fbe2e4d99e64d59fd9b7dd635fcc7057[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Wed Aug 10 11:14:57 2016 +0800

    Chairman to upload or take signature directly
    
    The chairman can now upload a signature or take a signature with
    the help of the web cam. However, signature is mirrored by default
    due to the web cam. Fixing it on the next commit.

[33mcommit 0232b658de22376cee3f005b39df96784e874c92[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Wed Aug 10 09:17:01 2016 +0800

    Set chairman display photo

[33mcommit 111b636dbe43e683f32c91038dd26ecfe9c5a175[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Tue Aug 9 21:22:44 2016 +0800

    Construct Barangay Official Setup Form
    
    Couldn't wait for JN to make the form...
    His academic spirit has departed weeks ago.

[33mcommit 9c59e8123f6bff12d2175b619e42659cfc9f606b[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Mon Aug 8 21:46:42 2016 +0800

    Fix Photoshop Control for Display Photo
    
    I've been sick for 3 days, so I wasn't able to finish this on time.
    But now, photoshop control now works for Display photo uploading and
    taking picture. Hoorah!
    
    Signature photos, you're next. . .

[33mcommit f783e620b86dffbd2c523ecf0d8ea820281c92c4[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Mon Aug 8 11:30:22 2016 +0800

    General controller to handle image manipulation
    
    WebcamCapture Control only works with 'take photo' feature of the
    resident.
    
    Create Photoshop control which will be the combination of
    WebcamCaptureControl (Take Picture with webcam)
    and SignatureAmendControl (Filters signature to remove background),
    and cropping features. Thus, it will serve as the universal
    control for all image manipulation.
    
    This controller is still under testing and development.

[33mcommit 251c83c010496a9f56bbbc60c9c72b35dd5c399a[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Thu Aug 4 11:54:42 2016 +0800

    Create Preference contract

[33mcommit 44b5087e3fa3962990825128187e0ed6d7907165[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Thu Aug 4 11:03:51 2016 +0800

    Feature update resident

[33mcommit f707421fcba022431ba5ec3965088ece87e0cdf6[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Wed Aug 3 20:45:55 2016 +0800

    Integrate Webcam capture to resident creation form
    
    Resident creation with webcam capture to take the photo of the
    resident now works! Next stop, resident update integration...

[33mcommit 531d92b9c9e67625208cca4a57f57e4a3a100a7f[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Wed Aug 3 19:26:15 2016 +0800

    Webcam capture partial support for Resident Form
    
    Fucking tiresome...

[33mcommit 2b093ccba3ec7323d17aa75db7b7ef64b8e80497[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Wed Aug 3 17:04:54 2016 +0800

    Create DraggableSquare class for image cropping
    
    Haaaa... I was afraid that the creation of this class will take a
    day or two. Finished it within half a day thanks to James_D's
    code snippet at stack overflow which I used as the basis for this
    class.

[33mcommit c6dd1675e6b08b6163bd0f47c5d0ce9db71dda59[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Tue Aug 2 22:26:44 2016 +0800

    Disable resident form dialog while upload window is active

[33mcommit 2ff11af6d177d70e6bd3c9a4caddccca4b5b3bbc[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Tue Aug 2 18:11:54 2016 +0800

    Build web cam capturing view

[33mcommit 2f0964d9ab382d83073d804ec355533167ba74b5[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Tue Aug 2 17:04:34 2016 +0800

    Add Webcam Capture API by Sarxos
    
    Added the webcam capture API to take photo using the
    internal or external webcam.

[33mcommit 887bc09842ccb70fc46eed7dd48492a0a00c68d1[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Tue Aug 2 11:17:55 2016 +0800

    Reset resident form after use

[33mcommit fdd0bac1ec0820870b4be19c0dd049c476f38896[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Tue Aug 2 07:23:20 2016 +0800

    Automatically select newly created resident

[33mcommit f72369b33adad2f41f349e8de34e05e30096abe3[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Mon Aug 1 22:19:05 2016 +0800

    Create a resident
    
    Creating a resident is now online! Next stop, updating a resident.

[33mcommit e13e4f86ad27c5ea5e2ec486365b3e781cabeb7c[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Mon Aug 1 18:43:50 2016 +0800

    Change resident form choice box to combo box
    
    Resident form creation is almost complete.
    
    All binary files will be stored in the public user, specifically in
    the directory Barangay131 which contains some other sub-directories
    to categorize the binary files.

[33mcommit 58832a8bbf84e735f1aa290b46939e49bd65d97c[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Fri Jul 29 20:49:25 2016 +0800

    Fix resident information collapsing
    
    The Resident address 1 collapses unto the address 2 when minimizing
    the stage. To solve the problem, The width of both the addresses are
    set to a static value of 450.

[33mcommit 5e3f388cc184a8e4d54d0b4f7926f2b7e6f31eb2[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Fri Jul 29 17:51:19 2016 +0800

    Represent date of residency with year and month
    
    A new column is added to the Resident table - month of residency -
    and renamed resident_since to year of residency.
    
    This is to improve the determination of the number of residency years
    of a resident, which will be needed in the barangay clearance.

[33mcommit ca84be6fdb2acaf52241d556b5884916cf4fa126[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Fri Jul 29 14:18:42 2016 +0800

    Delete, that is, archive selected resident
    
    Whoo! Finally did it. The main control acts as a wingman for the
    resident control to communicate with the resident deletion control,
    and vice-versa.
    
    All data manipulation regarding the resident must happen in the
    resident control.

[33mcommit cb98f02ba5ec39eeda744c4b1ce7d12ceceafe46[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Fri Jul 29 11:44:40 2016 +0800

    Rename CacheManager and DatabaseControl class
    
    Renamed CacheManager to CacheModel and DatabaseControl to
    DatabaseModel.
    
    In addition, configured DatabaseModel to be a global
    object to be passed around the scene controllers.

[33mcommit 465c300f10b6db43cd9de51e6afcf7a33846d3f8[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Thu Jul 28 17:24:34 2016 +0800

    Store resident selected in resident holder
    
    mResidentSelected is added to the resident scene, since it will be
    needed during the data presentation and update of the currently
    selected resident.

[33mcommit ce71327e68d99dd921aed71f7c370ce37e4c00a5[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Thu Jul 28 06:53:14 2016 +0800

    Create global cache manager
    
    The CacheManager class will now hold the cached data to be shared
    to the different scenes of the application, rather than having the
    different scenes have their own cached data.

[33mcommit dd233f172ed7e77647d6ea425219c7fca5b69159[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Wed Jul 27 21:34:56 2016 +0800

    Fix label selection limited to 0 - 39 residents.
    
    Wtf. I've been working the whole day creating the resident selection
    mechanism and I only just realized that selecting a label from the
    resident list paging is limited to only displaying the data of
    40 residents, particularly 0 - 39 residents. Created an integer
    that will calculate the 'real' index of the resident in the label
    selected. Thus, the index is not limited only to the values of 0 and 39.
    
    Also, mResidentSelectedIndex is renamed to mLabelSelectedIndex due
    to the fact that it represents the index (0 - 39) of each labels.

[33mcommit b9277ff3be7aac3eabc6c9093ee9a437fc7947b8[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Wed Jul 27 20:48:15 2016 +0800

    Display the selected resident data
    
    Redesigned the way on how the selected resident data is displayed.
    
    Enabled the selection and unselection of residents within the
    resident list paging.

[33mcommit 26cb19d2353d3b50ed24f834b89da2b39a710df0[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Wed Jul 27 09:45:45 2016 +0800

    Convert resident scene grid matrix(20x2) to a list
    
    Transformed the resident grid matrix to a list so that its structure
    matches that of the mResidentIds and mResidentNames. Thus, improving
    the way they work together.

[33mcommit 977fc83c4dce5d974524a3c5c7f5482a1dc4ce10[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Tue Jul 26 21:30:04 2016 +0800

    Build resident list paging and filter feature
    
    The resident list paging mechanism now works smoothly.
    
    Also, the resident list paging can now be filtered with the help of
    the search field.
    
    Furthermore, ListFilter class is created to handle the filtering
    mechanisms of the list paging of each information system.

[33mcommit 6e0b50e88f514860817edb626a14de6c44e5c508[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Mon Jul 25 21:11:21 2016 +0800

    Add Database Contract and Database Control
    
    The Database contract now holds a contract for the Resident table.
    
    The Database Control implements the pooling of connection to avoid
    the constant creation of connection which may cause performance
    issues.
    
    The Database Control will be used to store specialized queries to
    better suit the needs of the system while promoting code efficiency.
    
    Also, Resident Control now programmatically generates the labels of
    the pages.

[33mcommit fae5b10da0d7a524ec24f3e1a97da6930fdf7967[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Sat Jul 23 10:45:32 2016 +0800

    Transfer resources and add scenes Main and Resident
    
    Main and Resident scenes are created but not even close to
    being complete.

[33mcommit 9c645095478b21faaf126bf77b7027a78c4284f9[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Fri Jul 22 22:10:25 2016 +0800

    Add fundamental packages
    
    Different file types to be used in the projects include css, fxml,
    java and image files. Based from this information, we've created
    the following packages - css, fxml, javah, and res - to better
    organize the project.

[33mcommit 591bbec907cc6d136119265984516e87772d19c4[m
Author: Dominic Orga <dominicorga@gmail.com>
Date:   Fri Jul 22 22:03:47 2016 +0800

    We meet again, Barangay_131 system...
