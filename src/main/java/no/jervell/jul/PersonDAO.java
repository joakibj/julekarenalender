package no.jervell.jul;

import no.jervell.file.CSVFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arne C. Jervell (arne@jervell.no)
 */
public class PersonDAO
{
  private CSVFile source;

  private List<Person> personList = new ArrayList<Person>();

  public PersonDAO( CSVFile source ) throws IOException
  {
    this.source = source;
    loadAllData();
  }

  private void loadAllData()
  {
    int nRows = source.getRowCount();
    personList = new ArrayList<Person>( nRows );
    for ( int srcRowIndex = 0; srcRowIndex < nRows; ++srcRowIndex )
    {
      Person p = new Person();
      p.setName   ( source.get( srcRowIndex, "name" ) );
      p.setPicture( source.get( srcRowIndex, "picture" ) );
      p.setDay    ( source.get( srcRowIndex, "day", 0 ) );
      personList.add( p );
    }
  }

  public void persist() throws IOException
  {
    for ( int row = 0; row < personList.size(); ++row )
    {
      Person p = personList.get( row );
      source.set( row, "name", p.getName() );
      source.set( row, "picture", p.getPicture() );
      source.set( row, "day", p.getDay() <= 0 ? "" : String.valueOf(p.getDay()) );
    }
    source.save();
  }

  public List<Person> getPersonList()
  {
    return new ArrayList<Person>( personList );
  }
}
