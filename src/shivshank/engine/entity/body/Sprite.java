package shivshank.engine.entity.body;

import shivshank.engine.entity.Body;
import shivshank.engine.renderer.ModelInfo;

public class Sprite extends Body {

	public void fillModel(ModelInfo m) {
		
		m.assign(pos, uv, color, indices);
	}
}
