-- 
-- Database structure for word of life models
-- Version: v0.2
-- Last generated: 2024-03-21
--

CREATE DATABASE IF NOT EXISTS `word` 
	DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
USE `word`;

--	Bible	----------

-- Represents a Bible translation
-- name:         Name of this translation
-- abbreviation: A shortened version of this translation's name. 
-- 		Empty if there is no abbreviation.
-- created:      Time when this translation was added to this database
CREATE TABLE `translation`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`name` VARCHAR(16) NOT NULL, 
	`abbreviation` VARCHAR(3), 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX t_name_idx (`name`), 
	INDEX t_abbreviation_idx (`abbreviation`)
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a translated book of the Bible
-- book_id:        The translated book
-- 		References enumeration Book
-- 		Possible values are: 1 = genesis, 2 = exodus, 3 = leviticus, 4 = numbers, 5 = deuteronomy, 6 = joshua, 7 = judges, 8 = ruth, 9 = samuel 1, 10 = samuel 2, 11 = kings 1, 12 = kings 2, 13 = chronicles 1, 14 = chronicles 2, 15 = ezra, 16 = nehemiah, 17 = esther, 18 = job, 19 = psalms, 20 = proverbs, 21 = ecclesiastes, 22 = song of solomon, 23 = isaiah, 24 = jeremiah, 25 = lamentations, 26 = ezekiel, 27 = daniel, 28 = hosea, 29 = joel, 30 = amos, 31 = obadiah, 32 = jonah, 33 = micah, 34 = nahum, 35 = habakkuk, 36 = zephaniah, 37 = haggai, 38 = zechariah, 39 = malachi, 40 = matthew, 41 = mark, 42 = luke, 43 = john, 44 = acts, 45 = romans, 46 = corinthians 1, 47 = corinthians 2, 48 = galatians, 49 = ephesians, 50 = philippians, 51 = colossians, 52 = thessalonians 1, 53 = thessalonians 2, 54 = timothy 1, 55 = timothy 2, 56 = titus, 57 = philemon, 58 = hebrews, 59 = james, 60 = peter 1, 61 = peter 2, 62 = john 1, 63 = john 2, 64 = john 3, 65 = jude, 66 = revelation
-- translation_id: Id of the translation this book is part of
CREATE TABLE `book_translation`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`book_id` TINYINT NOT NULL, 
	`translation_id` INT NOT NULL, 
	INDEX bt_book_id_idx (`book_id`), 
	CONSTRAINT bt_t_translation_ref_fk FOREIGN KEY bt_t_translation_ref_idx (translation_id) REFERENCES `translation`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Lists the statements made within a book
-- book_id:      Id of the book where the statement appears
-- statement_id: Id of the statement made
-- order_index:  Index that indicates, where in the book the linked statement appears
CREATE TABLE `book_statement_link`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`book_id` INT NOT NULL, 
	`statement_id` INT NOT NULL, 
	`order_index` INT NOT NULL, 
	INDEX bsl_combo_1_idx (book_id, order_index), 
	CONSTRAINT bsl_bt_book_ref_fk FOREIGN KEY bsl_bt_book_ref_idx (book_id) REFERENCES `book_translation`(`id`) ON DELETE CASCADE, 
	CONSTRAINT bsl_s_statement_ref_fk FOREIGN KEY bsl_s_statement_ref_idx (statement_id) REFERENCES `statement`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a foot note made within the original text, concerning a specific word or a statement within the test
-- commented_statement_id: Id of the specific statement this footnote comments on
-- targeted_word_index:    A 0-based index that specifies the word targeted within this statement. None if no specific word was targeted.
CREATE TABLE `footnote`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`commented_statement_id` INT NOT NULL, 
	`targeted_word_index` INT, 
	CONSTRAINT f_bsl_commented_statement_ref_fk FOREIGN KEY f_bsl_commented_statement_ref_idx (commented_statement_id) REFERENCES `book_statement_link`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Locates a verse marker within a text
-- chapter_index:      A 1-based index that indicates which chapter this verse belongs to
-- verse_index:        A 1-based index that indicates which verse this is
-- first_statement_id: Id of the book statement that starts this verse
CREATE TABLE `verse_marker`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`chapter_index` INT NOT NULL, 
	`verse_index` INT NOT NULL, 
	`first_statement_id` INT NOT NULL, 
	INDEX vm_combo_1_idx (chapter_index, verse_index), 
	CONSTRAINT vm_bsl_first_statement_ref_fk FOREIGN KEY vm_bsl_first_statement_ref_idx (first_statement_id) REFERENCES `book_statement_link`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links a footnote to a statement made within it
-- footnote_id:  Id of the footnote where the statement is made
-- statement_id: Id of the statement made within the footnote
-- order_index:  A 0-based index that determines where the statement appears within the footnote
CREATE TABLE `footnote_statement_link`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`footnote_id` INT NOT NULL, 
	`statement_id` INT NOT NULL, 
	`order_index` INT NOT NULL, 
	INDEX fsl_combo_1_idx (footnote_id, order_index), 
	CONSTRAINT fsl_f_footnote_ref_fk FOREIGN KEY fsl_f_footnote_ref_idx (footnote_id) REFERENCES `footnote`(`id`) ON DELETE CASCADE, 
	CONSTRAINT fsl_s_statement_ref_fk FOREIGN KEY fsl_s_statement_ref_idx (statement_id) REFERENCES `statement`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

