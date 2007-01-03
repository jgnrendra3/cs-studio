package org.csstudio.archive.desy.aapi;


import org.csstudio.archive.ArchiveInfo;


/** Handles the "archives" request and its results.
 *  @author Albert Kagarmanov
 */
public class ArchivesRequest implements ClientRequest
{
	private ArchiveInfo archive_infos[];

	/** Read info from data server */
	public void read() //throws Exception
	{
		archive_infos = new ArchiveInfo[1];
//		archive_infos[0] =  new org.csstudio.archive.channelarchiver.ArchiveInfoImpl(
//		0,"AAPI","DESY_AAPI_SERVER");		
		//	{  int32 key,
		//     string name,
		//     string path }[] = archiver.archives()
	}

	/** @return Returns all the archive infos obtained in the request. */
    public ArchiveInfo[] getArchiveInfos()
	{
		return archive_infos;
	}

	/** @return Returns a more or less useful string. */
	@Override public String toString()
	{
		StringBuffer result = new StringBuffer();
        for (int i = 0; i < archive_infos.length; i++)
        	result.append(String.format("Key %4d: '%s' (%s)\n",
                archive_infos[i].getKey(),
                archive_infos[i].getName(),
                archive_infos[i].getDescription()));
		return result.toString();
	}
}
