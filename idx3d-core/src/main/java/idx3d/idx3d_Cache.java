// | -----------------------------------------------------------------
// | idx3d III is (c)1999/2000 by Peter Walser
// | -----------------------------------------------------------------
// | idx3d is a 3d engine written in 100% pure Java (1.1 compatible)
// | and provides a fast and flexible API for software 3d rendering
// | on the Java platform.
// |
// | Feel free to use the idx3d API / classes / source code for
// | non-commercial purposes (of course on your own risk).
// | If you intend to use idx3d for commercial purposes, please
// | contact me with an e-mail [proxima@active.ch].
// | -----------------------------------------------------------------

package idx3d;

public final class idx3d_Cache
{
	private CacheEntry head=null;
	
	public void put(Object val, long key)
	{
		CacheEntry c=new CacheEntry(val,key);
		c.next=head;
		head=c;
	}
	
	public Object get(long key)
	{
		CacheEntry current=head;
		CacheEntry last=null;
		
		while(current!=null)
		{
			if (current.key==key) 
			{
				if (last!=null)
				{
					last.next=current.next;
					current.next=head;
					head=current;
				}				
				return current.val;
			}
			last=current;
			current=current.next;
		}
		return null;
	}
	
	public void flush()
	{
		head=null;
	}	
}

class CacheEntry
{
	CacheEntry next;
	long key;
	Object val;
	
	public CacheEntry(Object val, long key)
	{
		this.val=val;
		this.key=key;
	}
}

