--
-- The initial database structure for this project
--

CREATE DATABASE living_word_database
    DEFAULT CHARACTER SET utf8
    DEFAULT COLLATE utf8_general_ci;
USE living_word_database;

-- Each row represents a singular word from the Bible
CREATE TABLE word(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `value` VARCHAR(32) NOT NULL,
    capitalize BOOLEAN NOT NULL DEFAULT FALSE,

    UNIQUE INDEX w_content_idx (`value`, capitalize)

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


-- Lists all characters (except for space) that are used for separating sentence segments from each other.
-- Contains 3 categories:
-- 1) Sentence terminators (., ? and !)
-- 2) Sentence part separators (;, : and possibly others)
-- 3) Sentence segment separators (,)
CREATE TABLE separator_character(
    `character` CHAR NOT NULL PRIMARY KEY

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
INSERT INTO separator_character (`character`) VALUES
    ('.'), ('?'), ('!'), (';'), (':'), (',');

-- Lists different books and letters of the Bible
CREATE TABLE writing(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a complete sentence (highest level)
-- Refers to the previous sentence, if there was one
CREATE TABLE sentence(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    context_id INT NOT NULL,
    order_index INT NOT NULL DEFAULT 0,

    UNIQUE INDEX s_sentence_order_idx (context_id, order_index),

    CONSTRAINT s_w_context_ref_fk FOREIGN KEY s_w_context_ref_idx (context_id)
        REFERENCES writing(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a sentence part. If there are multiple within a sentence, they are usually separated with : or ;
CREATE TABLE sentence_part(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    sentence_id INT NOT NULL,
    order_index INT NOT NULL DEFAULT 0,

    UNIQUE INDEX sp_order_idx (sentence_id, order_index),

    CONSTRAINT sp_s_link_to_parent_fk FOREIGN KEY sp_s_link_to_parent_idx (sentence_id)
        REFERENCES sentence(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents an individual sentence segment. When there are multiple in a sentence part,
-- they are often separated with ,
CREATE TABLE sentence_segment(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    sentence_part_id INT NOT NULL,
    order_index INT NOT NULL DEFAULT 0,
    terminator_char CHAR NOT NULL,
    parenthesis BOOLEAN NOT NULL DEFAULT FALSE,

    UNIQUE INDEX ss_order_idx (sentence_part_id, order_index),

    CONSTRAINT ss_sp_link_to_parent_fk FOREIGN KEY ss_sp_link_to_parent_idx (sentence_part_id)
        REFERENCES sentence_part(id) ON DELETE CASCADE,

    CONSTRAINT ss_sc_end_char_fk FOREIGN KEY ss_sc_end_char_idx (terminator_char)
        REFERENCES separator_character(`character`) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Shows each word's position in each sentence / sentence segment
CREATE TABLE word_assignment(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    word_id INT NOT NULL,
    location_id INT NOT NULL,
    order_index INT NOT NULL DEFAULT 0,

    UNIQUE INDEX wa_order_idx (location_id, order_index),

    CONSTRAINT wa_ss_link_to_location_fk FOREIGN KEY wa_ss_link_to_location_idx (location_id)
        REFERENCES sentence_segment(id) ON DELETE CASCADE,

    CONSTRAINT wa_w_assigned_word_fk FOREIGN KEY wa_w_assigned_word_idx (word_id)
        REFERENCES word(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Contains 3-letter codes assigned for different Bible books. E.g. Rev => Revelations of John
CREATE TABLE book_code(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(3) NOT NULL,
    book_id INT NOT NULL,

    UNIQUE INDEX bc_code_idx (code),

    CONSTRAINT bc_w_writing_ref_fk FOREIGN KEY bc_w_writing_ref_idx (book_id)
        REFERENCES writing(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Each row represents a single chapter (division) in a Bible book (E.g. the 3rd chapter of the Gospel of Mark)
CREATE TABLE chapter(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    book_id INT NOT NULL,
    number INT NOT NULL,

    UNIQUE INDEX c_chapter_order_idx (book_id, number),

    CONSTRAINT c_w_parent_writing_ref_fk FOREIGN KEY c_w_parent_writing_ref_idx (book_id)
        REFERENCES writing(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Each row represents a verse (marker) in a Bible book
-- Verses and chapters are used as addresses for text
CREATE TABLE verse(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    chapter_id INT NOT NULL,
    number INT NOT NULL,
    start_segment_id INT NOT NULL,

    UNIQUE INDEX v_verse_order_idx (chapter_id, number),

    CONSTRAINT v_c_parent_chapter_fk FOREIGN KEY v_c_parent_chapter_idx (chapter_id)
        REFERENCES chapter(id) ON DELETE CASCADE,

    CONSTRAINT v_ss_verse_start_ref_fk FOREIGN KEY v_ss_verse_start_ref_idx (start_segment_id)
        REFERENCES sentence_segment(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;