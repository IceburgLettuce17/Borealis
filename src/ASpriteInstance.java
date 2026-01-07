// ASpriteInstance.java - Aurora Sprite Instance
////////////////////////////////////////////////////////////////////////////////////////////////////
//
//  Author(s): Ionut Matasaru (ionut.matasaru@gameloft.com)
//
////////////////////////////////////////////////////////////////////////////////////////////////////

import javax.microedition.lcdui.*;

////////////////////////////////////////////////////////////////////////////////////////////////////

class ASpriteInstance
{
	int				_posX;			// position
	int				_posY;

	int				_pos_ox;		// offset added to position
	int				_pos_oy;

	int				_flags;			// flags

	ASprite			_sprite;
	int				_nCrtAnim;		// = _nCrtModule (if _nCrtTime < 0)
	int				_nCrtAFrame;	// = _nCrtFrame  (if _nCrtTime < 0)
	int				_nCrtTime;
	int[] 			_rect;

	ASpriteInstance	_parent;

	final static boolean DRAW_DEBUG_FRAMES = false;	// shows frame rect and origin point

////////////////////////////////////////////////////////////////////////////////////////////////////
/*
	static ASprite GetSprite(int index)
	{
		if (index < 0) return null;
		return Game._sprites[index];
	}
*/
////////////////////////////////////////////////////////////////////////////////////////////////////

	ASpriteInstance()
	{
		// all variables are initilaized with 0 (null)
	}

////////////////////////////////////////////////////////////////////////////////////////////////////

	ASpriteInstance(ASprite spr, int posX, int posY, ASpriteInstance parent)
	{
		_posX = (posX << DEF.FIXED_PRECISION);
		_posY = (posY << DEF.FIXED_PRECISION);

	//	_pos_ox = 0;
	//	_pos_oy = 0;

	//	_flags = 0;

		_sprite		= spr;
	//	_nCrtAnim	= 0;
	//	_nCrtAFrame	= 0;
	//	_nCrtTime	= 0;
		_rect		= new int[4];

		_parent = parent;
	}

////////////////////////////////////////////////////////////////////////////////////////////////////

	void SetAnim(int id)
	{
	//	if (DEF.bASSERT) DBG.ASSERT(_sprite != null);
	//	if (DEF.bASSERT) DBG.ASSERT((id >= 0) && (id < _sprite._anims_naf.length));
	//	if (DEF.bASSERT) DBG.ASSERT((id >= 0) && (id < _sprite._anims.length/2));

		if (DEF.bErr)
		{
			if (id < 0 || id >= _sprite._anims_naf.length)
			{
				if (DEF.bErr) System.out.println("ERROR: Anim out of range !");
				return;
			}
		}

		if (id != _nCrtAnim)
		{
			_nCrtAnim	= id;
			_nCrtAFrame	= 0;
			_nCrtTime	= 0;

			_pos_ox = 0;
			_pos_oy = 0;
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////

	void ApplyAnimOff()
	{
	//	if (DEF.bASSERT) DBG.ASSERT(_sprite != null);

		_posX -= _pos_ox;
		_posY -= _pos_oy;

		//////////

	//	_sprite->GetAFrameOffset(&_pos_ox, &_pos_oy);

		int off = (_sprite._anims_af_start[_nCrtAnim] + _nCrtAFrame) * 5;

	//	_pos_ox = ZOOM_OUT_FIXED_X(_sprite._aframes[off+2] << DEF.FIXED_PRECISION);
		_pos_ox = (_sprite._aframes[off+2] << DEF.FIXED_PRECISION) * DEF.ZOOM_X_DIV / DEF.ZOOM_X;
		if ((_flags & ASprite.FLAG_FLIP_X) != 0) _pos_ox = -_pos_ox;

	//	_pos_oy = ZOOM_OUT_FIXED_Y(_sprite._aframes[off+3] << DEF.FIXED_PRECISION);
		_pos_oy = (_sprite._aframes[off+3] << DEF.FIXED_PRECISION) * DEF.ZOOM_Y_DIV / DEF.ZOOM_Y;
		if ((_flags & ASprite.FLAG_FLIP_Y) != 0) _pos_oy = -_pos_oy;

		//////////

		_posX += _pos_ox;
		_posY += _pos_oy;
	}

////////////////////////////////////////////////////////////////////////////////////////////////////

	boolean	IsAnimEnded()
	{
	//	if (DEF.bASSERT) DBG.ASSERT(_sprite != null);

		if (_nCrtAFrame != _sprite.GetAFrames(_nCrtAnim) - 1)
			return false;

		int time = _sprite.GetAFrameTime(_nCrtAnim, _nCrtAFrame);

		return ((time == 0) || (_nCrtTime == time - 1));
	}

////////////////////////////////////////////////////////////////////////////////////////////////////

	private void PaintSprite(Graphics g)
	{
		if (_sprite == null)
			return;

		int posX = _posX,
			posY = _posY;
		for (ASpriteInstance o = _parent; o != null; o = o._parent)
		{
			posX += o._posX;
			posY += o._posY;
		}
		posX = ZOOM_IN_FIXED_X(posX) + DEF.SV_X;
		posY = ZOOM_IN_FIXED_Y(posY) + DEF.SV_Y;

	//	System.out.println("PaintSprite("+posX+", "+posY+")...");

		if (_nCrtTime >= 0)
			_sprite.PaintAFrame(g, _nCrtAnim, _nCrtAFrame, posX, posY, _flags, 0, 0);
		else if (_nCrtAnim >= 0) // _nCrtAnim --> module
			_sprite.PaintModule(g, _nCrtAnim, posX, posY, _flags);
		else if (_nCrtAFrame >= 0) // _nCrtAFrame --> frame
			_sprite.PaintFrame(g, _nCrtAFrame, posX, posY, _flags, 0, 0);

		if (DRAW_DEBUG_FRAMES)
		{
			int[] rect = GetRect();
			int w = ZOOM_IN_FIXED_X(rect[2] - rect[0]);
			int h = ZOOM_IN_FIXED_Y(rect[3] - rect[1]);
			g.setColor(0xFFFFFF00);
			g.drawRect(posX - ZOOM_IN_FIXED_X(_posX) + ZOOM_IN_FIXED_X(rect[0]),
					   posY - ZOOM_IN_FIXED_Y(_posY) + ZOOM_IN_FIXED_Y(rect[1]),
					   w, h);
			g.setColor(0xFFFF0000);
			g.drawRect(posX, posY, 1, 1);
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////

	void UpdateSpriteAnim()
	{
		if (_sprite == null)
			return;

		if (_nCrtTime < 0)
			return;
/*
		if ((Game._paused || Game._pause) &&
			((_flags & FLAG_UNPAUSABLE) == 0))
			return;

		if ((_flags & FLAG_PAUSE) != 0)
			return;
*/
		int time = _sprite.GetAFrameTime(_nCrtAnim, _nCrtAFrame);

		if (time == 0)
			return;

		_nCrtTime++;

		if (time > _nCrtTime)
			return;

		_nCrtTime = 0;

		_nCrtAFrame++;

		if (_nCrtAFrame >= _sprite.GetAFrames(_nCrtAnim))
		{
			_nCrtAFrame = 0;
			_pos_ox = 0;
			_pos_oy = 0;
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////
/*
	int[] GetRelRect()
	{
		if (_rect == null)
			_rect = new int[4];

		if (_sprite != null)
		{
			if (_nCrtTime >= 0)
				_sprite.GetAFrameRect(_rect, _nCrtAnim, _nCrtAFrame, 0, 0, _flags, 0, 0);
			else if (_nCrtAnim >= 0)
				_sprite.GetModuleRect(_rect, _nCrtAnim, 0, 0, _flags);
			else if (_nCrtAFrame >= 0)
				_sprite.GetFrameRect(_rect, _nCrtAFrame, 0, 0, _flags, 0, 0);
		}
		else
		{
			if (_type == DEF.OBJTYPE_TRIGGER)
			{
				_rect[0] = 0;
				_rect[1] = 0;
				_rect[2] = _data[DI.TRI_EndX] - _posX;
				_rect[3] = _data[DI.TRI_EndY] - _posY;
			}
		}

		return _rect;
	}
*/
////////////////////////////////////////////////////////////////////////////////////////////////////

	int[] GetRect()
	{
		if (_rect == null)
			_rect = new int[4];

		if (_sprite != null)
		{
			if (_nCrtTime >= 0)
				_sprite.GetAFrameRect(_rect, _nCrtAnim, _nCrtAFrame, _posX, _posY, _flags, 0, 0);
			else if (_nCrtAnim >= 0)
				_sprite.GetModuleRect(_rect, _nCrtAnim, _posX, _posY, _flags);
			else if (_nCrtAFrame >= 0)
				_sprite.GetFrameRect(_rect, _nCrtAFrame, _posX, _posY, _flags, 0, 0);
		}
		else
		{
			//...
		}

		return _rect;
	}

////////////////////////////////////////////////////////////////////////////////////////////////////

	static boolean IsRectCrossing(int rect1[], int rect2[])
	{
		if (rect1[0] > rect2[2]) return false;
		if (rect1[2] < rect2[0]) return false;
		if (rect1[1] > rect2[3]) return false;
		if (rect1[3] < rect2[1]) return false;
		return true;
	}

////////////////////////////////////////////////////////////////////////////////////////////////////

	static boolean IsPointInRect(int x, int y, int rect[])
	{
		if (x < rect[0]) return false;
		if (x > rect[2]) return false;
		if (y < rect[1]) return false;
		if (y > rect[3]) return false;
		return true;
	}

////////////////////////////////////////////////////////////////////////////////////////////////////

	private int _vis; // visibility flags

	void OnScreenTest()
	{
	//	_vis = 1 + 2 + 4 + 8;

		int ox = 0, oy = 0;
		for (ASpriteInstance o = _parent; o != null; o = o._parent)
		{
			ox += o._posX;
			oy += o._posY;
		}

		int[] rect = GetRect();

		rect[0] += ox - (DEF.GV_W << DEF.FIXED_PRECISION);
		rect[1] += oy - (DEF.GV_H << DEF.FIXED_PRECISION);
		rect[2] += ox;
		rect[3] += oy;

		_vis = 0;

		if (rect[0] < 0 && rect[2] >= 0 && rect[1] < 0 && rect[3] >= 0)
			_vis = 1 + 2 + 4 + 8;
		else
		{
			//...
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////

	boolean	IsOnScreen(int style)
	{
		return ((_vis & style) != 0);
	}

////////////////////////////////////////////////////////////////////////////////////////////////////

//	static int ZOOM_IN_X(int x) { return (((x)*DEF.ZOOM_X)/DEF.ZOOM_X_DIV); }
//	static int ZOOM_IN_Y(int y) { return (((y)*DEF.ZOOM_Y)/DEF.ZOOM_Y_DIV); }

//	static int ZOOM_OUT_X(int x) { return (((x)*DEF.ZOOM_X_DIV)/DEF.ZOOM_X); }
//	static int ZOOM_OUT_Y(int y) { return (((y)*DEF.ZOOM_Y_DIV)/DEF.ZOOM_Y); }

	static int ZOOM_IN_FIXED_X(int x) { return ((((x)>>DEF.FIXED_PRECISION)*DEF.ZOOM_X)/DEF.ZOOM_X_DIV); }
	static int ZOOM_IN_FIXED_Y(int y) { return ((((y)>>DEF.FIXED_PRECISION)*DEF.ZOOM_Y)/DEF.ZOOM_Y_DIV); }

//	static int ZOOM_OUT_FIXED_X(int x) { return ((((x)<<DEF.FIXED_PRECISION)*DEF.ZOOM_X_DIV)/DEF.ZOOM_X); }
//	static int ZOOM_OUT_FIXED_Y(int y) { return ((((y)<<DEF.FIXED_PRECISION)*DEF.ZOOM_Y_DIV)/DEF.ZOOM_Y); }

////////////////////////////////////////////////////////////////////////////////////////////////////

} // class ASpriteInstance

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
