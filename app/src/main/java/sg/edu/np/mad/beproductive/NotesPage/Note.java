package sg.edu.np.mad.beproductive.NotesPage;

public class Note {

    public String note_header;
    public String note_content;

    public void setNote_header(String title) { this.note_header = title; }
    public void setNote_content(String content) { this.note_content = content; }

    public String getNote_header() {return note_header;}
    public String getNote_content() {return note_content;}

    public Note(String header, String content){
        this.note_header = header;
        this.note_content = content;
    }
}
