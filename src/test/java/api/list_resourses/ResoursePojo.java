package api.list_resourses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ResoursePojo {
    private Integer id;
    private String name;
    private Integer year;
    private String color;
    private String pantone_value;
}
