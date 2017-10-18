package net.imglib2.roi.labeling;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.IntType;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

/**
 * @author Matthias Arzt
 */
public class ImgLabelingTest {

	@Test
	public void testCreateFromImageAndLabelSets() {
		// setup
		Img<IntType> image = ArrayImgs.ints(new int[]{1, 0, 2}, 3);
		String[] values = {"A", "B"};
		String[] valuesB = {"Hello", "World"};
		List<Set<String>> labelSets = Arrays.asList(asSet(), asSet(values), asSet(valuesB));
		// process
		ImgLabeling<String, IntType> labeling = ImgLabeling.fromImageAndLabelSets(image, labelSets);
		// test
		RandomAccess<LabelingType<String>> ra = labeling.randomAccess();
		ra.setPosition(new long[]{0});
		assertEquals(asSet(values), ra.get());
		ra.setPosition(new long[]{1});
		assertEquals(Collections.emptySet(), ra.get());
		ra.setPosition(new long[]{2});
		assertEquals(asSet(valuesB), ra.get());
	}

	@Test
	public void testCreateFromImageAndLabels() {
		// setup
		Img<IntType> image = ArrayImgs.ints(new int[]{3}, 1);
		List<String> labels = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
		// process
		ImgLabeling<String, IntType> labeling = ImgLabeling.fromImageAndLabels(image, labels);
		// test
		RandomAccess<LabelingType<String>> ra = labeling.randomAccess();
		ra.setPosition(new long[]{0});
		assertEquals(asSet("c"), ra.get());
	}

	@Test
	public void testModifyCreatedImgLabeling() {
		// setup
		int[] data = {2};
		Img<IntType> image = ArrayImgs.ints(data, 1);
		List<Set<String>> labelSets = Arrays.asList(asSet(), asSet("1","2"), asSet("1"));
		ImgLabeling<String, IntType> labeling = ImgLabeling.fromImageAndLabelSets(image, labelSets);
		RandomAccess<LabelingType<String>> ra = labeling.randomAccess();
		ra.setPosition(new long[]{0});
		// process
		ra.get().add("2");
		// test
		assertEquals(asSet("1", "2"), ra.get());
		assertEquals(1, data[0]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreatedImgLabelingRepeatingLabel() {
		Img<IntType> image = ArrayImgs.ints(new int[]{2}, 1);
		ImgLabeling<String, IntType> labeling = ImgLabeling.fromImageAndLabels(image, Arrays.asList("1", "1"));
	}

	private <T> Set<T> asSet(T... values) {
		return new TreeSet<>(Arrays.asList(values));
	}
}
