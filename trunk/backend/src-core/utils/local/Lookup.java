/* This file is part of Delivery Manager.
 * (c) 2006 Matteo Miraz, Politecnico di Milano
 *
 * Delivery Manager is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation; either version 2 of the License, or 
 * (at your option) any later version.
 *
 * Delivery Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Delivery Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


package utils.local;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Lookup {
    private static InitialContext ctx;

	@SuppressWarnings("unchecked")
	public static <I,C extends I> I get(Class<I> type, Class<C> bean) throws NamingException {
		if(ctx == null) ctx = new InitialContext();
		System.out.println("Looking for deliveryManager/" + bean.getSimpleName() + "/local");
		return (I) ctx.lookup("deliveryManager/" + bean.getSimpleName() + "/local");
	}

	@SuppressWarnings("unchecked")
	public static <I> I getByName(@SuppressWarnings("unused") Class<I> type, String name) throws NamingException {
		if(ctx == null) ctx = new InitialContext();
		return (I) ctx.lookup(name);
	}
}
