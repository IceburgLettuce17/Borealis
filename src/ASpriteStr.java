// ASpriteStr.java - Aurora Sprite - MIDP 2.0 version
////////////////////////////////////////////////////////////////////////////////////////////////////
//
//  Author(s): Ionel Petcu (ionel.petcu@gameloft.com)
//
////////////////////////////////////////////////////////////////////////////////////////////////////
//
//  Contains methods to handle displaying of strings.
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.Sprite; // just for MIDP2 transformations

////////////////////////////////////////////////////////////////////////////////////////////////////

class ASpriteStr
{
	ASprite _sprFontM;
	ASprite _sprFontS;
	ASprite _sprFontB;

	ASpriteStr()
	{

	}
    short[] WraptextB(String s, int x_start, int y_start, int width, int height, int char_height)
    {
        short sizes[] = null;
        int str_len = s.length();
        int strLines = (str_len*(Game.font._modules_w[1]&0xFF))/width;
        short lineSize =0;
        short cnt = 1;
        short lastSpacePos = 0;
        short distFromLastSpacePos = 0;
        width -= Game.font._modules_w[0]&0xFF;

        int lines = 0;
        for (int i = 0; i < str_len; i++)
        {
            int c = s.charAt(i);
            if (c == '\n')
            {
                lines++;
            }
        }

        sizes = new short[2*strLines+1 + lines];

        for (int i = 0; i < str_len; i++)
        {
            int c = s.charAt(i);
            if (c == ' ')
            {
                lineSize += (Game.font._modules_w[0]&0xFF) + Game.font._fmodules[1];
                lastSpacePos = (short)i;
                distFromLastSpacePos = 0;

                if(lineSize >= width)
                {
                   sizes[cnt++] = (short)(lastSpacePos + 1);
                   sizes[cnt++] = (short)(lineSize - distFromLastSpacePos);
                   lineSize = 0;
                   i = lastSpacePos;
                }
                continue;
            }
            else if (c == '\n')
            {
                sizes[cnt++] = (short)i;
                sizes[cnt++] = (short)lineSize;
                lineSize = 0;
                continue;
            }
            else if (c < 32)
            {
                if (c == '\u0001') // auto change current palette
                {
                    i++;
                //	_cur_pal = s.charAt(i);
                    continue;
                }
                else if (c == '\u0002') // select fmodule
                {
                    i++;
                    c = s.charAt(i);
                }
                else continue;
            }
            else
                c = Game.font._map_char[c]&0xFF;

            if (c >= Game.font.GetFModules(0))
            {
                if (DEF.bErr) System.out.println("Character not available: c = "+c);
                c = 0;
            }

            int m = (Game.font._fmodules[c<<2]&0xFF)<<1;

            if (m >= Game.font._modules_w.length)
            {
                if (DEF.bErr) System.out.println("Character module not available: c = "+c+"  m = "+(m>>1));
                m = 0;
                c = 0;
            }

            int charSize = (Game.font._modules_w[m]&0xFF) - Game.font._fmodules[(c<<2)+1] + Game.font._fmodules[1];
            distFromLastSpacePos +=charSize;
            lineSize += charSize;

            if(lineSize >= width)
            {
               sizes[cnt++] = (short)(lastSpacePos + 1);
               sizes[cnt++] = (short)(lineSize - distFromLastSpacePos);
               lineSize = 0;
               i = lastSpacePos;
            }
        }
        if(lineSize != 0)
        {
           sizes[cnt++] = (short)str_len;
           sizes[cnt++] = (short)lineSize;
           lineSize = 0;
        }

//        if(lineSize >= width)
//             sizes[cnt] =  lineSize;
        sizes[0] = (short)(cnt/2);
        return sizes;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    void DrawPageB(Graphics g, String s, short[] sizes, int x, int y, int startLine, int maxLines, int maxchar, int anchor)
    {
        // Count lines...
        int lines = sizes[0];

        int th = Game.font.GetLineSpacing() + maxchar;

             if ((anchor & Graphics.BOTTOM)  != 0)	y -= (th * (lines-1));
        else if ((anchor & Graphics.VCENTER) != 0)	y -= (th * (lines-1)) >> 1;

        // Draw each line...
        int k=0;
        for (int j = startLine; j < lines; j++, k++)
        {
            if(k > maxLines-1)
                break;

            ASprite._index1 = (j > 0) ? sizes[(j-1)*2+1] : 0;
            if(s.charAt(ASprite._index1) == '\n')
                ASprite._index1++;
            ASprite._index2 = sizes[j*2+1];

            int xx = x;
            int yy = y + k * th;

            if ((anchor & (Graphics.RIGHT | Graphics.HCENTER | Graphics.BOTTOM | Graphics.VCENTER)) != 0)
            {
                     if ((anchor & Graphics.RIGHT)   != 0)	xx -= sizes[(j+1)*2];
                else if ((anchor & Graphics.HCENTER) != 0)	xx -= sizes[(j+1)*2]>>1;
                     if ((anchor & Graphics.BOTTOM)  != 0)	yy -= (Game.font._modules_h[1]&0xFF);
                else if ((anchor & Graphics.VCENTER) != 0)	yy -= (Game.font._modules_h[1]&0xFF)>>1;
            }
//           System.out.println("str size calc := " + sizes[(j+1)*2]);
           Game.font.DrawString(g, s, xx, yy, 0);
//            DrawString(g, s, x, y + k * th, anchor);
        }

        // Disable substring...
        ASprite._index1 = -1;
        ASprite._index2 = -1;
    }




	String Wraptext(String s, int x_start, int y_start, int width, int height, int char_height, int bigFnt)
	  {
		  String StrText="";
//        int char_height = GetStringMaxHeight(s);
	  s.replace('#', '\n');
		  char[] text = s.toCharArray();
		  setPagesAndRows(text, x_start, y_start, width, height, bigFnt, char_height);
		  StrText = "";
		  for(int k=0;k<text.length;k++)
			  StrText+=text[k];
			  return StrText;
	  }


	  int setPagesAndRows(char [] b, int x_start, int y_start, int w, int h, int bigFnt, int spacing)
	  {
		  int i = 0;

		  int y = y_start;
		  int pages = 0;
		  int length = b.length;

		  if (b == null)
			  return pages;

		  StringBuffer sb = new StringBuffer();

		  for (i = 0; i < b.length && b[i] != 0; i++)
		  {
			  if (b[i] == 0xd)
				  sb.append( ' ' );
			  else if (b[i] == '\n')
			  {
				  if(i>1 && b[i-1] != ' ')
					  sb.append( ' ' );
				  b[i] = 0x20;//space
			  }
			  else
				  sb.append( (char) b[i] );
		  }

		  String s = sb.toString();
		  sb = null;

		  if (DEF.bDbgO)
		  {
			  System.out.print( s );

			  System.out.println( " " );
		  }

		  int startRow = 0;
		  int space1 = -1;
		  int space2 = -1;
		  space1 = s.indexOf( ' ', startRow );

		  while (true)
		  {
			  if (space1 == -1)
				  break;

			  space2 = -1;
			  for(int cnt=(space1+1);cnt<length;cnt++)
			  {
				  if(b[cnt] == 32)
				  {
					  space2 = cnt;
					  break;
				  }
			  }
			  String s1;

			  if (space2 == -1)
				  s1 = s.substring( startRow );
			  else
				  s1 = s.substring( startRow, space2 );

			  if (DEF.bDbgO)
			  {
				  System.out.println( s1 );
			  }

			  int text_width = 0;

			if (bigFnt == 0)
				  _sprFontM.UpdateStringSize( s1 );
			  else
				  if (bigFnt == 1)
					  _sprFontS.UpdateStringSize( s1 );
				  else
					  _sprFontB.UpdateStringSize( s1 );

			  if (text_width > w)
			  {
				  b[space1] = '\n';

				  startRow = space1 + 1;
				  y += spacing;

				  if (y > h)
				  {
					  b[space1] = '\n';

					  pages++;
					  y = y_start;
				  }

				  space1 = space2;
			  }
			  else
			  {
				  space1 = space2;
			  }
		  }

		  return pages + 1;
	  }

}

