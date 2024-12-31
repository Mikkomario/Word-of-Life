# Word Of Life
Version: **v0.2**  
Updated: 2024-12-30

## Table of Contents
- [Enumerations](#enumerations)
  - [Book](#book)
- [Packages & Classes](#packages-and-classes)
  - [Bible](#bible)
    - [Book Statement Link](#book-statement-link)
    - [Book Translation](#book-translation)
    - [Footnote](#footnote)
    - [Footnote Statement Link](#footnote-statement-link)
    - [Translation](#translation)
    - [Verse Marker](#verse-marker)
  - [Text](#text)
    - [Statement Placement](#statement-placement)
    - [Text Placement](#text-placement)

## Enumerations
Below are listed all enumerations introduced in Word Of Life, in alphabetical order  

### Book
An enumeration for different books that appear within the Bible

Key: `id: Int`  

**Values:**
- **Genesis** (1)
- **Exodus** (2)
- **Leviticus** (3)
- **Numbers** (4)
- **Deuteronomy** (5)
- **Joshua** (6)
- **Judges** (7)
- **Ruth** (8)
- **Samuel 1** (9)
- **Samuel 2** (10)
- **Kings 1** (11)
- **Kings 2** (12)
- **Chronicles 1** (13)
- **Chronicles 2** (14)
- **Ezra** (15)
- **Nehemiah** (16)
- **Esther** (17)
- **Job** (18)
- **Psalms** (19)
- **Proverbs** (20)
- **Ecclesiastes** (21)
- **Song Of Solomon** (22)
- **Isaiah** (23)
- **Jeremiah** (24)
- **Lamentations** (25)
- **Ezekiel** (26)
- **Daniel** (27)
- **Hosea** (28)
- **Joel** (29)
- **Amos** (30)
- **Obadiah** (31)
- **Jonah** (32)
- **Micah** (33)
- **Nahum** (34)
- **Habakkuk** (35)
- **Zephaniah** (36)
- **Haggai** (37)
- **Zechariah** (38)
- **Malachi** (39)
- **Matthew** (40)
- **Mark** (41)
- **Luke** (42)
- **John** (43)
- **Acts** (44)
- **Romans** (45)
- **Corinthians 1** (46)
- **Corinthians 2** (47)
- **Galatians** (48)
- **Ephesians** (49)
- **Philippians** (50)
- **Colossians** (51)
- **Thessalonians 1** (52)
- **Thessalonians 2** (53)
- **Timothy 1** (54)
- **Timothy 2** (55)
- **Titus** (56)
- **Philemon** (57)
- **Hebrews** (58)
- **James** (59)
- **Peter 1** (60)
- **Peter 2** (61)
- **John 1** (62)
- **John 2** (63)
- **John 3** (64)
- **Jude** (65)
- **Revelation** (66)

Utilized by the following 1 classes:
- [Book Translation](#book-translation)

## Packages and Classes
Below are listed all classes introduced in Word Of Life, grouped by package and in alphabetical order.  
There are a total number of 2 packages and 8 classes

### Bible
This package contains the following 6 classes: [Book Statement Link](#book-statement-link), [Book Translation](#book-translation), [Footnote](#footnote), [Footnote Statement Link](#footnote-statement-link), [Translation](#translation), [Verse Marker](#verse-marker)

#### Book Statement Link
Links a statement to a book in which it is made

##### Details
- Combines with [Verse Marker](#verse-marker), creating a **Verse Beginning Statement Link**
- Uses a **combo index**: `book_id`

##### Properties
Book Statement Link contains the following 1 properties:
- **Book Id** - `bookId: Int` - Id of the book where the statement appears
  - Refers to [Book Translation](#book-translation)

##### Referenced from
- [Footnote](#footnote).`commentedStatementId`
- [Verse Marker](#verse-marker).`firstStatementId`

#### Book Translation
Represents a translated specific book of the Bible

##### Details
- Combines with [Translation](#translation), creating a **Contextual Book**
- Uses **index**: `book_id`

##### Properties
Book Translation contains the following 2 properties:
- **Book** - `book: Book` - The translated book
- **Translation Id** - `translationId: Int` - Id of the translation this book is part of
  - Refers to [Translation](#translation)

##### Referenced from
- [Book Statement Link](#book-statement-link).`bookId`

#### Footnote
Represents a foot note made within the original text, concerning a specific word or a statement within the text

##### Details
- Combines with multiple [Footnote Statement Links](#footnote-statement-link), creating a **Footnote With Links**

##### Properties
Footnote contains the following 2 properties:
- **Commented Statement Id** - `commentedStatementId: Int` - Id of the specific statement this footnote comments on
  - Refers to [Book Statement Link](#book-statement-link)
- **Targeted Word Index** - `targetedWordIndex: Option[Int]` - A 0-based index that specifies the word targeted within this statement. 
None if no specific word was targeted.

##### Referenced from
- [Footnote Statement Link](#footnote-statement-link).`parentId`

#### Footnote Statement Link
Links a footnote to a statement made within it

##### Details

##### Properties
Footnote Statement Link contains the following 1 properties:
- **Parent Id** - `parentId: Int` - Id of the footnote where the statement is made
  - Refers to [Footnote](#footnote)

#### Translation
Represents a translation covering one or more Bible books

##### Details
- Uses 2 database **indices**: `name`, `abbreviation`

##### Properties
Translation contains the following 3 properties:
- **Name** - `name: String` - Name of this translation
- **Abbreviation** - `abbreviation: String` - A shortened version of this translation's name. 
Empty if there is no abbreviation.
- **Created** - `created: Instant` - Time when this translation was added to this database

##### Referenced from
- [Book Translation](#book-translation).`translationId`

#### Verse Marker
Locates a verse marker within a text

##### Details
- Uses a **combo index**: `chapter_index` => `verse_index`

##### Properties
Verse Marker contains the following 3 properties:
- **Chapter Index** - `chapterIndex: Int` - A 1-based index that indicates which chapter this verse belongs to
- **Verse Index** - `verseIndex: Int` - A 1-based index that indicates which verse this is
- **First Statement Id** - `firstStatementId: Int` - Id of the book statement that starts this verse
  - Refers to [Book Statement Link](#book-statement-link)

### Text
This package contains the following 2 classes: [Statement Placement](#statement-placement), [Text Placement](#text-placement)

#### Statement Placement
Common trait for models which are used for placing statements within various texts

##### Details

##### Properties
Statement Placement contains the following 3 properties:
- **Parent Id** - `parentId: Int` - Id of the text where the placed text appears
- **Statement Id** - `statementId: Int` - Id of the statement which appears within the linked text
  - Refers to *statement* from another module
- **Order Index** - `orderIndex: Int`, `0` by default - 0-based index that indicates the specific location of the placed text

#### Text Placement
Places some type of text to some location within another text

##### Details

##### Properties
Text Placement contains the following 3 properties:
- **Parent Id** - `parentId: Int` - Id of the text where the placed text appears
- **Placed Id** - `placedId: Int` - Id of the text that is placed within the parent text
- **Order Index** - `orderIndex: Int`, `0` by default - 0-based index that indicates the specific location of the placed text
